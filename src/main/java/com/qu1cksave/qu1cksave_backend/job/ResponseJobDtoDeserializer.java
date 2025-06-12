package com.qu1cksave.qu1cksave_backend.job;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

// Deserialize:
// https://www.baeldung.com/jackson-object-mapper-tutorial
// https://www.baeldung.com/jackson-nested-values
// https://www.baeldung.com/jackson-deserialization
//
// Convert to actual type
// https://stackoverflow.com/questions/75923197/how-to-convert-a-json-property-to-an-object-using-objectmapper
//
// NOTE: (6/12/25) Will just use JsonProperty instead
public class ResponseJobDtoDeserializer extends StdDeserializer<ResponseJobDto> {
    public ResponseJobDtoDeserializer() {
        this(null);
    }

    public ResponseJobDtoDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ResponseJobDto deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {

        JsonNode node = jp.getCodec().readTree(jp);

        JsonNode idNode = node.get("id");
        JsonNode memberIdNode = node.get("member_id");
        JsonNode resumeIdNode = node.get("resume_id");
        JsonNode coverLetterIdNode = node.get("cover_letter_id");
        JsonNode titleNode = node.get("title");
        JsonNode companyNameNode = node.get("company_name");
        JsonNode jobDescriptionNode = node.get("job_description");
        JsonNode notesNode = node.get("notes");
        JsonNode isRemoteNode = node.get("is_remote");
        JsonNode salaryMinNode = node.get("salary_min");
        JsonNode salaryMaxNode = node.get("salary_max");
        JsonNode countryNode = node.get("country");
        JsonNode usStateNode = node.get("us_state");
        JsonNode cityNode = node.get("city");
        JsonNode dateSavedNode = node.get("date_saved");

        JsonNode dateAppliedNode = node.get("date_applied");
        JsonNode dateAppliedYearNode = !dateAppliedNode.isNull() ? dateAppliedNode.get("year"): null;
        JsonNode dateAppliedMonthNode = !dateAppliedNode.isNull() ? dateAppliedNode.get("month"): null;
        JsonNode dateAppliedDateNode = !dateAppliedNode.isNull() ? dateAppliedNode.get("date"): null;
        String dateAppliedString = !dateAppliedNode.isNull() ? "\"date_applied\": { \"year\": " +
            (dateAppliedYearNode != null && !dateAppliedYearNode.isNull() ? dateAppliedYearNode.asInt() : null) +
            ",\"month\": " + (dateAppliedMonthNode != null && !dateAppliedMonthNode.isNull() ? dateAppliedMonthNode.asInt() : null) +
            ",\"date\": " + (dateAppliedDateNode != null && !dateAppliedDateNode.isNull() ? dateAppliedDateNode.asInt() : null) + "}"
            : null;
//        STR."""
//            "date_applied": {
//                "year": \{!dateAppliedYearNode.isNull() ? dateAppliedYearNode.asInt() : null},
//                "month": \{!dateAppliedMonthNode.isNull() ? dateAppliedMonthNode.asInt() : null},
//                "date": \{!dateAppliedDateNode.isNull() ? dateAppliedDateNode.asInt() : null}
//            }
//        """;

        JsonNode datePostedNode = node.get("date_posted");
        JsonNode datePostedYearNode = !datePostedNode.isNull() ? datePostedNode.get("year"): null;
        JsonNode datePostedMonthNode = !datePostedNode.isNull() ? datePostedNode.get("month"): null;
        JsonNode datePostedDateNode = !datePostedNode.isNull() ? datePostedNode.get("date"): null;
        String datePostedString = !datePostedNode.isNull() ? "\"date_posted\": { \"year\": " +
            (datePostedYearNode != null && !datePostedYearNode.isNull() ? datePostedYearNode.asInt() : null) +
            ",\"month\": " + (datePostedMonthNode != null && !datePostedMonthNode.isNull() ? datePostedMonthNode.asInt() : null) +
            ",\"date\": " + (datePostedDateNode != null && !datePostedDateNode.isNull() ? datePostedDateNode.asInt() : null) + "}"
            : null;

        JsonNode jobStatusNode = node.get("job_status");
        JsonNode linksNode = node.get("links");
        JsonNode foundFromNode = node.get("found_from");
        JsonNode resumeNode = node.get("resume");
        JsonNode coverLetterNode = node.get("cover_letter");

        // Note: id, memberId, title, companyName, isRemote, dateSaved,
        //   and jobStatus can't be null

        // NOTE: Need to add public setters when using this approach
//        ResponseJobDto responseJobDto = new ResponseJobDto();
//        ObjectMapper objectMapper = new ObjectMapper();
//        responseJobDto.setId(idNode != null && !idNode.isNull() ? UUID.fromString(idNode.asText()) : null);
//        responseJobDto.setMemberId(memberIdNode != null && !memberIdNode.isNull() ? UUID.fromString(memberIdNode.asText()) : null);
//        responseJobDto.setResumeId(resumeIdNode != null && !resumeIdNode.isNull() ? UUID.fromString(resumeIdNode.asText()) : null);
//        responseJobDto.setCoverLetterId(coverLetterIdNode != null && !coverLetterIdNode.isNull() ? UUID.fromString(coverLetterIdNode.asText()) : null);
//        responseJobDto.setTitle(titleNode != null && !titleNode.isNull() ? titleNode.asText() : null);
//        responseJobDto.setCompanyName(companyNameNode != null && !companyNameNode.isNull() ? companyNameNode.asText() : null);
//        responseJobDto.setJobDescription(jobDescriptionNode != null && !jobDescriptionNode.isNull() ? jobDescriptionNode.asText() : null);
//        responseJobDto.setNotes(notesNode != null && !notesNode.isNull() ? notesNode.asText() : null);
//        responseJobDto.setIsRemote(isRemoteNode != null && !isRemoteNode.isNull() ? isRemoteNode.asText() : null);
//        responseJobDto.setSalaryMin(salaryMinNode != null && !salaryMinNode.isNull() ? salaryMinNode.asInt() : null);
//        responseJobDto.setSalaryMax(salaryMaxNode != null && !salaryMaxNode.isNull() ? salaryMaxNode.asInt() : null);
//        responseJobDto.setCountry(countryNode != null && !countryNode.isNull() ? countryNode.asText() : null);
//        responseJobDto.setUsState(usStateNode != null && !usStateNode.isNull() ? usStateNode.asText() : null);
//        responseJobDto.setCity(cityNode != null && !cityNode.isNull() ? cityNode.asText() : null);
//        responseJobDto.setDateSaved(dateSavedNode != null && !dateSavedNode.isNull() ? dateSavedNode.asText(): null);
//
//        //  - Why is Jackson insistent on using a constructor to create a YearMonthDate instance?
//        //    -- org.springframework.core.codec.DecodingException: JSON decoding error: Cannot construct instance of `com.qu1cksave.qu1cksave_backend.job.YearMonthDateDto` (although at least one Creator exists): no String-argument constructor/factory method to deserialize from String value ('date_applied')
//        //    -- This isn't a problem in ResponseJobDto's constructor
//        //  - BUT FIRST, instead of using readValue, just create the object
//        //    yourself instead
//        //    -- I still get  org.springframework.core.codec.DecodingException: JSON decoding error: No content to map due to end-of-input
//        //        at app//org.springframework.http.codec.json.AbstractJackson2Decoder.processException(AbstractJackson2Decoder.java:282)
//        YearMonthDateDto dateAppliedYmd = !dateAppliedNode.isNull() ?
//            new YearMonthDateDto(
//                dateAppliedYearNode != null && !dateAppliedYearNode.isNull() ? dateAppliedYearNode.asInt() : null,
//                dateAppliedMonthNode != null && !dateAppliedMonthNode.isNull() ? dateAppliedMonthNode.asInt() : null,
//                dateAppliedDateNode != null && !dateAppliedDateNode.isNull() ? dateAppliedDateNode.asInt() : null
//            ) : null;
//        YearMonthDateDto datePostedYmd = !datePostedNode.isNull() ?
//            new YearMonthDateDto(
//                datePostedYearNode != null && !datePostedYearNode.isNull() ? datePostedYearNode.asInt() : null,
//                datePostedMonthNode != null && !datePostedMonthNode.isNull() ? datePostedMonthNode.asInt() : null,
//                datePostedDateNode != null && !datePostedDateNode.isNull() ? datePostedDateNode.asInt() : null
//            ) : null;
//        responseJobDto.setDateApplied(dateAppliedYmd);
//        responseJobDto.setDatePosted(datePostedYmd);
//        responseJobDto.setJobStatus(jobStatusNode != null && !jobStatusNode.isNull() ? jobStatusNode.asText() : null);
//        responseJobDto.setLinks(linksNode != null && !linksNode.isNull() ? objectMapper.readValue(linksNode.asText(), String[].class) : null); // TODO: Convert to String[]
//        responseJobDto.setFoundFrom(foundFromNode != null && !foundFromNode.isNull() ? foundFromNode.asText() : null);
//        responseJobDto.setResume(resumeNode != null && !resumeNode.isNull() ? objectMapper.readValue(resumeNode.asText(), ResponseResumeDto.class) : null); // TODO: Convert to ResponseResumeDto
//        responseJobDto.setCoverLetter(coverLetterNode != null && !coverLetterNode.isNull() ? objectMapper.readValue(coverLetterNode.asText(), ResponseCoverLetterDto.class) : null); // TODO: Convert to ResponseCoverLetterDto

//        return responseJobDto;
        // Returning null since I'm not using this deserializer and I don't
        //   want to add the public setters
        return null;
    }
}