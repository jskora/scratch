package scratch.jackson;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@SuppressWarnings("Duplicates")
public class JsonCreatorExample {

    @Test
    public void test1() throws IOException {
        String json1 = "{\"name\":\"granite\", \"attribGP\":5}";
        Object obj1 = new ObjectMapper()
                .readerFor(GrandParent.class)
                .readValue(json1);
        assertThat(obj1, instanceOf(GrandParent.class));
        GrandParent entity1 = (GrandParent) obj1;
        assertEquals("granite", entity1.getName());
        assertEquals(Integer.valueOf(5), entity1.getAttribGP());
    }

    @Test
    public void test2() throws IOException {
        String json2 = "{\"name\":\"hardwood\",\"attribGP\":100,\"attribP\":\"tight\"}";
        Object obj2 = new ObjectMapper()
                .readerFor(Parent.class)
                .readValue(json2);
        assertThat(obj2, instanceOf(Parent.class));
        Parent entity2 = (Parent)obj2;
        assertEquals("hardwood", entity2.getName());
        assertEquals(Integer.valueOf(100), entity2.getAttribGP());
        assertEquals("tight", entity2.getAttribP());
    }

    @Test
    public void test3() throws IOException {
        String json3 = "{\"name\":\"pine\",\"attribGP\":80,\"attribP\":\"open\", \"attribChild\":\"pacific\"}";
        Object obj3 = new ObjectMapper()
                .readerFor(Child.class)
                .readValue(json3);
        assertThat(obj3, instanceOf(Child.class));
        Child entity3 = (Child)obj3;
        assertEquals("pine", entity3.getName());
        assertEquals(Integer.valueOf(80), entity3.getAttribGP());
        assertEquals("open", entity3.getAttribP());
        assertEquals("pacific", entity3.getAttribChild());
    }

    @Test(expected = ClassCastException.class)
    public void test4aBadReader() throws IOException {
        String json4a = "{\"name\":\"pine\",\"attribGP\":80,\"attribP\":\"open\", \"attribChild\":\"pacific\"}";
        Object obj4a = new ObjectMapper()
                .readerFor(Child.class)
                .readValue(json4a);
        assertThat(obj4a, instanceOf(Child.class));
        GrandChild entity4a = (GrandChild)obj4a;
    }

    @Test(expected = InvalidDefinitionException.class)
    public void test4bNoInject() throws IOException {
        String json4b = "{\"name\":\"pine\",\"attribGP\":80,\"attribP\":\"open\", \"attribChild\":\"pacific\"}";
        Object obj4b = new ObjectMapper()
                .readerFor(GrandChild.class)
                .readValue(json4b);
        assertThat(obj4b, instanceOf(Child.class));
        GrandChild entity4b = (GrandChild)obj4b;
        assertEquals("pine", entity4b.getName());
        assertEquals(Integer.valueOf(80), entity4b.getAttribGP());
        assertEquals("open", entity4b.getAttribP());
        assertEquals("pacific", entity4b.getAttribChild());
        assertEquals("default", entity4b.getAttribInject());
    }

    @Test
    public void test4c() throws IOException {
        String json4c = "{\"name\":\"pine\",\"attribGP\":80,\"attribP\":\"open\", \"attribChild\":\"pacific\"}";
        Object obj4c = new ObjectMapper()
                .reader(new InjectableValues.Std().addValue(String.class, "default"))
                .forType(GrandChild.class)
                .readValue(json4c);
        assertThat(obj4c, instanceOf(Child.class));
        GrandChild entity4c = (GrandChild)obj4c;
        assertEquals("pine", entity4c.getName());
        assertEquals(Integer.valueOf(80), entity4c.getAttribGP());
        assertEquals("open", entity4c.getAttribP());
        assertEquals("pacific", entity4c.getAttribChild());
        assertEquals("default", entity4c.getAttribInject());
    }

    private static class GrandParent {
        private String name;
        private Integer attribGP;

        @JsonCreator
        public GrandParent(@JsonProperty("name") String name,
                           @JsonProperty("attribGP") Integer attribGP) {
            this.name = name;
            this.attribGP = attribGP;
        }

        public String getName() {
            return name;
        }

        public Integer getAttribGP() {
            return attribGP;
        }
    }

    private static class Parent extends GrandParent {
        private String attribP;

        @JsonCreator
        public Parent(@JsonProperty("name") String name,
                      @JsonProperty("attribGP") Integer attribGP,
                      @JsonProperty("attribP") String attribP) {
            super(name, attribGP);
            this.attribP = attribP;
        }
        public String getAttribP() {
            return attribP;
        }
    }

    private static class Child extends Parent {
        private String attribChild;

        @JsonCreator
        public Child(@JsonProperty("name") String name,
                     @JsonProperty("attribGP") Integer attribGP,
                     @JsonProperty("attribP") String attribP,
                     @JsonProperty("attribChild") String attribChild) {
            super(name, attribGP, attribP);
            this.attribChild = attribChild;
        }
        public String getAttribChild() {
            return attribChild;
        }
    }

    private static class GrandChild extends Child {
        @JacksonInject
        private String attribInject;
        public GrandChild(@JsonProperty("name") String name,
                          @JsonProperty("attribGP") Integer attribGP,
                          @JsonProperty("attribP") String attribP,
                          @JsonProperty("attribChild") String attribChild) {
            super(name, attribGP, attribP, attribChild);
        }
        public String getAttribInject() {
            return attribInject;
        }
    }
}
