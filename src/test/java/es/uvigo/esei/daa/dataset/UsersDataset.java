package es.uvigo.esei.daa.dataset;

import static java.util.Arrays.binarySearch;
import static java.util.Arrays.stream;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Predicate;

import java.util.Base64;

import es.uvigo.esei.daa.entities.User;

public final class UsersDataset {
	private UsersDataset() {
	}

	public static User[] users() {
		return new User[] {
				new User(1, "login1","713bfda78870bf9d1b261f565286f85e97ee614efe5f0faf7c34e7ca4f65baca"),
				new User(2, "login2","713bfda78870bf9d1b261f565286f85e97ee614efe5f0faf7c34e7ca4f65baca"),
				new User(3, "login3","713bfda78870bf9d1b261f565286f85e97ee614efe5f0faf7c34e7ca4f65baca"),
				new User(4, "login4","713bfda78870bf9d1b261f565286f85e97ee614efe5f0faf7c34e7ca4f65baca"),
				new User(5, "login5","8b2c86ea9cf2ea4eb517fd1e06b74f399e7fec0fef92e3b482a6cf2e2b092023"),
		
		};
	}
	
	public static User[] UserNoExistentPage() {
		return new User[] {};
	}

	public static User[] UsersWithout(int... ids) {
		Arrays.sort(ids);

		final Predicate<User> hasValidId = user -> binarySearch(ids, user.getId()) < 0;

		return stream(users()).filter(hasValidId).toArray(User[]::new);
	}

	public static User user(int id) {
		return stream(users()).filter(user -> user.getId() == id).findAny()
				.orElseThrow(IllegalArgumentException::new);
	}

	public static int existentId() {
		return 5;
	}

	public static int nonExistentId() {
		return 1234;
	}

	public static User existentUser() {
		return user(existentId());
	}

	public static User nonExistentUser() {
		return new User(nonExistentId(), "login6","password6");
	}

	public static String newLogin() {
		return "login5";
	}

	public static String newPassword() {
		return "8b2c86ea9cf2ea4eb517fd1e06b74f399e7fec0fef92e3b482a6cf2e2b092023";
	}

	public static User newUser() {
		return new User(5, newLogin(),newPassword());
		
	}
	

}
