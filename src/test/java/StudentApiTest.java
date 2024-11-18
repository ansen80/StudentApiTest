import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class StudentApiTest {

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void testGetStudentNotFound() {
        given()
                .when()
                .get("/student/555")
                .then()
                .statusCode(404); // Студент с id 555 не найден
    }

    @Test
    public void testAddNewStudent() {
        String newStudent = """
            {
                "id": null,
                "name": "Andrey",
                "marks": [5, 4, 3]
            }
        """;

        Response response = given()
                .header("Content-Type", "application/json")
                .body(newStudent)
                .when()
                .post("/student");

        response.then()
                .statusCode(201) // Успешное добавления студента
                .body("id", notNullValue()); // Генерация id студента
    }

    @Test
    public void testUpdateStudent() {
        String updatedStudent = """
            {
                "id": 1,
                "name": "Ivan",
                "marks": [4, 4, 4]
            }
        """;

        given()
                .header("Content-Type", "application/json")
                .body(updatedStudent)
                .when()
                .post("/student")
                .then()
                .statusCode(201) // Обновление прошло успешно
                .body("id", equalTo(1)); // ID совпадает
    }

    @Test
    public void testDeleteStudent() {
        given()
                .when()
                .delete("/student/1")
                .then()
                .statusCode(200); // Успешное удаление
    }

    @Test
    public void testGetTopStudent() {
        Response response = given()
                .when()
                .get("/topStudent");

        response.then()
                .statusCode(200); // Успешный ответ

        if (!response.getBody().asString().isEmpty()) {
            response.then()
                    .body("id", notNullValue())
                    .body("name", notNullValue())
                    .body("marks", not(emptyArray())); // Проверка на существование оценок
        }
    }
}
