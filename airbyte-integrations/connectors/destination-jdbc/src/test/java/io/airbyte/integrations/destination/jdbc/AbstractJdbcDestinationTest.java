package io.airbyte.integrations.destination.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.airbyte.commons.json.Jsons;
import io.airbyte.integrations.destination.StandardNameTransformer;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class AbstractJdbcDestinationTest {

  private JsonNode buildConfigNoJdbcParameters() {
    final JsonNode config = Jsons.jsonNode(ImmutableMap.of(
        "host", "localhost",
        "port", 1337,
        "username", "user",
        "database", "db"
    ));
    return config;
  }

  private JsonNode buildConfigWithExtraJdbcParameters(final String extraParam) {
    final JsonNode config = Jsons.jsonNode(ImmutableMap.of(
        "host", "localhost",
        "port", 1337,
        "username", "user",
        "database", "db",
        "jdbc_url_params", extraParam
    ));
    return config;
  }

  @Test
  void testNoExtraParamsNoDefault() {
    final Map<String, String> connectionProperties = new TestJdbcDestination().getConnectionProperties(buildConfigNoJdbcParameters());

    final Map<String, String> expectedProperties = ImmutableMap.of();
    assertEquals(expectedProperties, connectionProperties);
  }

  @Test
  void testNoExtraParamsWithDefault() {
    final Map<String, String> defaultProperties = ImmutableMap.of("A_PARAMETER", "A_VALUE");

    final Map<String, String> connectionProperties = new TestJdbcDestination(defaultProperties).getConnectionProperties(
        buildConfigNoJdbcParameters());

    assertEquals(defaultProperties, connectionProperties);
  }

  @Test
  void testExtraParamNoDefault() {
    final String extraParam = "key1=value1&key2=value2&key3=value3";
    final Map<String, String> connectionProperties = new TestJdbcDestination().getConnectionProperties(
        buildConfigWithExtraJdbcParameters(extraParam));
    final Map<String, String> expectedProperties = ImmutableMap.of(
        "key1", "value1",
        "key2", "value2",
        "key3", "value3");
    assertEquals(expectedProperties, connectionProperties);
  }

  @Test
  void testExtraParamWithDefault() {
    final Map<String, String> defaultProperties = ImmutableMap.of("A_PARAMETER", "A_VALUE");
    final String extraParam = "key1=value1&key2=value2&key3=value3";
    final Map<String, String> connectionProperties = new TestJdbcDestination(defaultProperties).getConnectionProperties(
        buildConfigWithExtraJdbcParameters(extraParam));
    final Map<String, String> expectedProperties = ImmutableMap.of(
        "A_PARAMETER", "A_VALUE",
        "key1", "value1",
        "key2", "value2",
        "key3", "value3");
    assertEquals(expectedProperties, connectionProperties);
  }

  @Test
  void testExtraParameterEqualToDefault() {
    final Map<String, String> defaultProperties = ImmutableMap.of("key1", "value1");
    final String extraParam = "key1=value1&key2=value2&key3=value3";
    final Map<String, String> connectionProperties = new TestJdbcDestination(defaultProperties).getConnectionProperties(
        buildConfigWithExtraJdbcParameters(extraParam));
    final Map<String, String> expectedProperties = ImmutableMap.of(
        "key1", "value1",
        "key2", "value2",
        "key3", "value3");
    assertEquals(expectedProperties, connectionProperties);
  }

  @Test
  void testExtraParameterDiffersFromDefault() {
    final Map<String, String> defaultProperties = ImmutableMap.of("key1", "value0");
    final String extraParam = "key1=value1&key2=value2&key3=value3";

    assertThrows(IllegalArgumentException.class, () ->
        new TestJdbcDestination(defaultProperties).getConnectionProperties(
            buildConfigWithExtraJdbcParameters(extraParam))
    );
  }

  @Test
  void testInvalidExtraParam() {
    final String extraParam = "key1=value1&sdf&";
    assertThrows(IllegalArgumentException.class,
        () -> new TestJdbcDestination().getConnectionProperties(buildConfigWithExtraJdbcParameters(extraParam)));
  }

  static class TestJdbcDestination extends AbstractJdbcDestination {

    private final Map<String, String> defaultProperties;

    public TestJdbcDestination() {
      this(new HashMap<>());
    }

    public TestJdbcDestination(final Map<String, String> defaultProperties) {
      super("", new StandardNameTransformer(), new TestJdbcSqlOperations());
      this.defaultProperties = defaultProperties;
    }

    @Override
    protected Map<String, String> getDefaultConnectionProperties(final JsonNode config) {
      return defaultProperties;
    }

    @Override
    public JsonNode toJdbcConfig(final JsonNode config) {
      return config;
    }
  }
}
