/*
 * Copyright (c) 2021 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.integrations.destination.s3.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import io.airbyte.commons.json.Jsons;
import io.airbyte.commons.resources.MoreResources;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class AvroRecordHelperTest {

  @Test
  public void testModifyJsonRecordWithCorrectDateTime() throws IOException {

    final JsonNode jsonSchema = Jsons.deserialize(MoreResources.readResource("avro/jsonSchemaWithManyNestedObjects.json"));
    final JsonNode jsonWithStringDateTime = Jsons.deserialize(MoreResources.readResource("avro/dateTimeString.json"));
    final JsonNode jsonWithAvroDateTime = Jsons.deserialize(MoreResources.readResource("avro/dateTimeModified.json"));
    JsonNode jsonNode = Jsons.clone(jsonWithStringDateTime);
    AvroRecordHelper.transformDateTimeInJson(jsonSchema, jsonNode);
    assertEquals(jsonWithAvroDateTime, jsonNode);

  }

}
