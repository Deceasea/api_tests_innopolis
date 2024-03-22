package lesson1;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GetToDoList extends ApiTests {

    @Test
    public void testGetTodoList() {
        // Отправка GET-запроса для получения списка задач
        Response response = RestAssured.get();
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getContentType().contains("application/json"));
        assertTrue(response.getBody().asString().length() > 0);
        assertNotNull(response.getHeader("Server"));
    }

    @Test
    public void testInvalidURL() {
        // Попытка отправки GET-запроса к несуществующему URL
        Response response = RestAssured.get("https://todo-ap-sky.herokuapp.com/");
        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void testInvalidRequest() {
        // Отправка GET-запроса с неверным форматом запроса
        Response response = RestAssured.post("https://todo-app-sky.herokuapp.com/");
        assertEquals(400, response.getStatusCode());
    }
}
