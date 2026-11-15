package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {

	@Autowired
	private FilmController filmController;

	@Autowired
	private UserController userController;

	@Test
	void contextLoads() {
		assertNotNull(filmController);
		assertNotNull(userController);
	}

	@Test
	void createValidFilm() {
		Film film = new Film();
		film.setName("Интерстеллар");
		film.setDescription("Научная фантастика");
		film.setReleaseDate(LocalDate.of(2014, 11, 6));
		film.setDuration(169);

		Film created = filmController.create(film);
		assertNotNull(created.getId());
		assertEquals("Интерстеллар", created.getName());
	}

	@Test
	void filmValidationFailures() {
		Film film = new Film();
		film.setName("Film");
		film.setReleaseDate(LocalDate.of(1800, 1, 1)); // Раньше 1895
		film.setDuration(100);

		assertThrows(ValidationException.class, () -> filmController.create(film));
	}

	@Test
	void userEmptyNameShouldFallbackToLogin() {
		User user = new User();
		user.setEmail("test@ya.ru");
		user.setLogin("vittorio");
		user.setBirthday(LocalDate.of(1994, 4, 1));

		User created = userController.create(user);
		assertEquals("vittorio", created.getName());
	}
}