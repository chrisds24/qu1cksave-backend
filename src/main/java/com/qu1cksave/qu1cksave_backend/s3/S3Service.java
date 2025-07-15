package com.qu1cksave.qu1cksave_backend.s3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Component
public class S3Service {
    private final S3Client s3Client;
    private final String bucketName = System.getenv("BUCKET_NAME");
    private final String envType = System.getenv("ENV_TYPE");

    public S3Service(@Autowired S3Client s3Client) {
        this.s3Client = s3Client;
    }

    // https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/s3/S3Client.html
    // - Refer to this to look at methods, return types, etc.
    public void putObject(
        UUID key,
        double[] byteArrayAsArray
    ) {
        // If in a production environment, use this code. Otherwise, we don't
        //   want to mess around with the production S3 bucket.
        if (Objects.equals(envType, "PROD")) {
//            System.out.println("******** WARNING !!!!!!!!!!!: Using PROD mode for putObject");
//            System.out.println("PUTTING object with id: " + key);
            // Note: The try-catch is in the job service file to allow it or any
            //   other service that uses this method to customize the error message
            // https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/core/sync/RequestBody.html
            // - RequestBody
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(String.valueOf(key))
                    .build(),
                RequestBody.fromBytes(
                    doubleArrToByteArr(byteArrayAsArray)
                )
            );
        } else {
//            System.out.println("************** DEV mode putObject.");
            // This doesn't really return anything, but I'll just do the conversion
            //   from double[] to byte[], then from that byte[] back to double[]
            //   here.
//            System.out.println(
//                "Given double[]: " + Arrays.toString(byteArrayAsArray)
//            );

            byte[] bytes = doubleArrToByteArr(byteArrayAsArray);
//            System.out.println(
//                "Converted to byte[]: " + Arrays.toString(bytes)
//            );

            double[] doubles = byteArrToDoubleArr(bytes);
//            System.out.println(
//                "From byte[] back to double[]: " + Arrays.toString(doubles)
//            );
        }
    }

    public void deleteObject(UUID key) {
        if (Objects.equals(envType, "PROD")) {
//            System.out.println("******** WARNING !!!!!!!!!!!: Using PROD mode for deleteObject");
//            System.out.println("DELETING object with id: " + key);
            s3Client.deleteObject(
                DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(String.valueOf(key))
                    .build()
            );
        } else {
//            System.out.println("******** DEV MODE deleteObject");
        }
        // If not in production mode, doesn't do anything
    }

    public double[] getObject(UUID key) {
        if (Objects.equals(envType, "PROD")) {
//            System.out.println("******** WARNING !!!!!!!!!!!: Using PROD mode for getObject");
//            System.out.println("GETTING object with id: " + key);
            return byteArrToDoubleArr(
                s3Client.getObjectAsBytes(
                    GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(String.valueOf(key))
                        .build()
                ).asByteArray()
            );
        } else {
            // If not in production mode, simply returns this array (which is
            //   used by the tests)
//            System.out.println("********** DEV mode getObject");
            return new double[]{2, 4, 7, 10, 14};
        }
    }

    // https://stackoverflow.com/questions/13071777/convert-double-to-byte-array
    // - Has something about ByteBuffer
    // - ChatGPT also uses this
    // https://stackoverflow.com/questions/16892580/convert-array-of-doubles-to-bytearray-in-java
    // - Also uses ByteBuffer + has code for converting from byte[] to double[]
    public static byte[] doubleArrToByteArr(double[] byteArrayAsArray) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(
            byteArrayAsArray.length * Double.BYTES
        ); // Double.BYTES = 8
        for (double d : byteArrayAsArray) {
            byteBuffer.putDouble(d);
        }
        return byteBuffer.array();
    }

    public static double[] byteArrToDoubleArr(byte[] bytes) {
        double[] byteArrayAsArray = new double[bytes.length / Double.BYTES];
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        for (int i = 0; i < byteArrayAsArray.length; i++) {
            byteArrayAsArray[i] = byteBuffer.getDouble();
        }
        return byteArrayAsArray;
    }
}

// TODO: After the mocked S3 calls (using Mockito) are done for the tests, I
//   should leave the check for PROD to make sure that I don't use the
//   production S3 bucket accidentally.
//  - On the other hand, accidentally modifying the production database in RDS
//    isn't an issue since it can only be affected if I set my database env
//    variables to match its name/credentials.
//    -- I'll just keep setting my AWS env credentials so I don't have to
//       rewrite my code.

// NOTES: (7/6/25)
//  -------- AWS SDK for Java ---------
//  https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials-explicit.html
//  - Creating an S3Client using supplied credentials via StaticCredentialsProvider
//    -- https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/auth/credentials/StaticCredentialsProvider.html
//       + API reference
//  https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started-tutorial.html
//  - Put and delete examples
//  https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/singleton-service-clients.html
//  - Singleton instances of an S3Client
//    -- If you use dependency injection frameworks like Spring, configure service clients as singleton beans.
//       This ensures proper lifecycle management.
//    -- https://medium.com/codex/aws-s3-in-spring-boot-ca4b173e9cb1
//       + I can just return S3Client.build()..... as a bean
//  https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/region-selection.html
//  - Region newRegion = Region.of("us-east-42"), so I can set region in the builder
//  https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/using.html
//  - Examples
//  https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/handling-exceptions.html
//  - Has a link to S3 Exceptions I think


// TODO: Off topic
//  - How to do the following in Java/Spring?
//app.use(cors());
//// app.use(express.json());
//// https://stackoverflow.com/questions/60947294/error-413-payload-too-large-when-upload-image
//// https://gist.github.com/Maqsim/857a14a4909607be13d6810540d1b04f
//// https://stackoverflow.com/questions/19917401/error-request-entity-too-large (Used this one)
//    app.use(express.json({limit: '2mb'}));
//// app.use(express.json({limit: '300kb'}));
//    app.use(express.urlencoded({ extended: false }));


