package lesson1;

import io.restassured.RestAssured;
import org.junit.BeforeClass;

public class ApiTests {
    public static void main(String[] args) {
    }

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://todo-app-sky.herokuapp.com/";
    }
}
