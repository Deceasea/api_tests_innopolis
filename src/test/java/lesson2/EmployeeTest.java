package lesson2;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;


public class EmployeeTest {
    public static final String URL_EMPLOYEE = "https://x-clients-be.onrender.com/employee";
    public static final String URL_ID_COMPANY = "https://x-clients-be.onrender.com/company/{id}";
    public static final String URL_COMPANY = "https://x-clients-be.onrender.com/company";
    public static final String URL_AUTH = "https://x-clients-be.onrender.com/auth/login";
    public static final String URL = "https://x-clients-be.onrender.com/";

    public static String TOKEN;
    public static int COMPANY_ID;
    public static int EMPLOYEE_ID;
    public static int NEW_EMPLOYEE_ID;

    //Создадим данные для тестов по сотрудникам
    @BeforeAll
    public static void getTokenCompanyEmployee() {
        String creds = """
                {
                  "username": "tecna",
                  "password": "tecna-fairy"
                }
                """;

        // Авторизация
        TOKEN = given()
                .log().all()
                .body(creds)
                .contentType(ContentType.JSON)
                .when()
                .post(URL_AUTH)
                .then()
                .log().all()
                .statusCode(201)
                .extract().path("userToken");

        String myJson = """ 
                {
                  "name": "AC/DC"
                }
                """;

        // Создание компании
        COMPANY_ID = given()
                .log().all()
                .body(myJson)
                .header("x-client-token", TOKEN)
                .contentType(ContentType.JSON)
                .when().post(URL_COMPANY)
                .then()
                .log().all()
                .statusCode(201)
                .log().all()
                .body("id", notNullValue())
                .extract().path("id");

        // Проверяем, что компания создалась
        given()
                .log().all()
                .pathParams("id", COMPANY_ID)
                .get(URL_ID_COMPANY)
                .then()
                .log().all()
                .statusCode(200)
                .body("name", equalTo("AC/DC"));

        //Добавим сотрудника для тестов
        String createEmployee = "{\"firstName\": \"Ivan\",\"lastName\": \"Ivanov\",\"companyId\": " + COMPANY_ID + ",\"email\": \"ivanov@mail.ru\",\"phone\": \"1234567890\"}";
        // Создание сотрудника
        EMPLOYEE_ID = given()
                .log().all()
                .body(createEmployee)
                .header("x-client-token", TOKEN)
                .contentType(ContentType.JSON)
                .when().post(URL_EMPLOYEE)
                .then()
                .log().all()
                .statusCode(201)
                .log().all()
                .body("id", notNullValue())
                .extract().path("id");
    }

    //Добавим нового сотрудника
    @Test
    public void addNewEmployee() {
        String newEmployee = "{\"firstName\": \"Petr\",\"lastName\": \"Petrov\",\"companyId\": " + COMPANY_ID + ",\"email\": \"petrov@mail.ru\",\"phone\": \"0987654321\"}";
        // Создание сотрудника
        NEW_EMPLOYEE_ID = given()
                .log().all()
                .body(newEmployee)
                .header("x-client-token", TOKEN)
                .contentType(ContentType.JSON)
                .when()
                .post(URL_EMPLOYEE)
                .then()
                .log().all()
                .statusCode(201)
                .log().all()
                .body("id", notNullValue())
                .extract().path("id");

        // Проверяем, что сотрудник создался
        given()
                .log().all()
                .queryParam("id", NEW_EMPLOYEE_ID)
                .get(URL_EMPLOYEE + "/" + NEW_EMPLOYEE_ID)
                .then()
                .log().all()
                .statusCode(200)
                .body("id", equalTo(NEW_EMPLOYEE_ID));
    }

    // Получим список сотрудников и проверим, что там есть наш созданный тестовый сотрудник
    @Test
    public void GetEmployeeList() {
        given()
                .log().all()
                .queryParam("company", COMPANY_ID)
                .get(URL_EMPLOYEE)
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", hasItem(EMPLOYEE_ID))
                .body("companyId", hasItem(COMPANY_ID));
    }

    //Получим сотрудника по ID
    @Test
    public void checkEmployeeID() {
        given()
                .log().all()
                .header("x-client-token", TOKEN)
                .contentType(ContentType.JSON)
                .queryParam("employee", EMPLOYEE_ID)
                .get(URL_EMPLOYEE + "/" + EMPLOYEE_ID)
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(EMPLOYEE_ID))
                .body("companyId", equalTo(COMPANY_ID));
    }

    //Сотрудник не найден
    @Test
    public void notFoundEmployee() {
        given()
                .log().all()
                .header("x-client-token", TOKEN)
                .contentType(ContentType.JSON)
                .queryParam("employee", "asdfgh")
                .get(URL)
                .then()
                .log().all()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("error", equalTo("Not Found"));
    }

    //Изменим информацию о сотруднике
    @Test
    public void changeEmployeeInfo() {
        String newInfo = """ 
                {
                  "lastName": "Smirnov",
                  "email": "smirnov@mail.ru",
                  "phone": "67890432",
                  "isActive": true
                }
                """;

        given()
                .log().all()
                .header("x-client-token", TOKEN)
                .contentType(ContentType.JSON)
                .queryParam("employee", EMPLOYEE_ID)
                .body(newInfo)
                .patch(URL_EMPLOYEE + "/" + EMPLOYEE_ID)
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", equalTo(EMPLOYEE_ID))
                .body("email", equalTo("smirnov@mail.ru"));
    }
}

