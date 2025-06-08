package com.qu1cksave.qu1cksave_backend.job;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

// Deserialize:
// https://www.baeldung.com/jackson-object-mapper-tutorial
// https://www.baeldung.com/jackson-nested-values
//
// Convert to actual type
// https://stackoverflow.com/questions/75923197/how-to-convert-a-json-property-to-an-object-using-objectmapper
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
        JsonNode datePostedNode = node.get("date_posted");
        JsonNode jobStatusNode = node.get("job_status");
        JsonNode linksNode = node.get("links");
        JsonNode foundFromNode = node.get("found_from");
        JsonNode resumeNode = node.get("resume");
        JsonNode coverLetterNode = node.get("cover_letter");

        // Note: id, memberId, title, companyName, isRemote, dateSaved,
        //   and jobStatus can't be null

        return new ResponseJobDto(
            idNode != null && !idNode.isNull() ? UUID.fromString(idNode.asText()) : null, // UUID
            memberIdNode != null && !memberIdNode.isNull() ? UUID.fromString(memberIdNode.asText()) : null, // UUID
            resumeIdNode != null && !resumeIdNode.isNull() ? UUID.fromString(resumeIdNode.asText()) : null, // UUID
            coverLetterIdNode != null && !coverLetterIdNode.isNull() ? UUID.fromString(coverLetterIdNode.asText()) : null, // UUID
            titleNode != null && !titleNode.isNull() ? titleNode.asText() : null, // String
            companyNameNode != null && !companyNameNode.isNull() ? companyNameNode.asText() : null, // String
            jobDescriptionNode != null && !jobDescriptionNode.isNull() ? jobDescriptionNode.asText() : null, // String
            notesNode != null && !notesNode.isNull() ? notesNode.asText() : null, // String
            isRemoteNode != null && !isRemoteNode.isNull() ? isRemoteNode.asText() : null, // String
            salaryMinNode != null && !salaryMinNode.isNull() ? salaryMinNode.asInt() : null, // Integer
            salaryMaxNode != null && !salaryMaxNode.isNull() ? salaryMaxNode.asInt() : null, // Integer
            countryNode != null && !countryNode.isNull() ? countryNode.asText() : null, // String
            usStateNode != null && !usStateNode.isNull() ? usStateNode.asText() : null, // String
            cityNode != null && !cityNode.isNull() ? cityNode.asText() : null, // String
            dateSavedNode != null && !dateSavedNode.isNull() ? Instant.parse(dateSavedNode.asText()) : null, // Instant
            dateAppliedNode != null && !dateAppliedNode.isNull() ? dateAppliedNode.asText() : null, // String
            datePostedNode != null && !datePostedNode.isNull() ? datePostedNode.asText() : null, // String
            jobStatusNode != null && !jobStatusNode.isNull() ? jobStatusNode.asText() : null, // String
            linksNode != null && !linksNode.isNull() ? linksNode.asText() : null, // String
            foundFromNode != null && !foundFromNode.isNull() ? foundFromNode.asText() : null, // String
            resumeNode != null && !resumeNode.isNull() ? resumeNode.asText() : null, // String
            coverLetterNode != null && !coverLetterNode.isNull() ? coverLetterNode.asText() : null // String
        );
    }
}
