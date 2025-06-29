package com.qu1cksave.qu1cksave_backend;

public class TestAuthInputs {
    public static String testExistentCredentials = """
			{
				"email": "molly@books.com",
				"password": "mollymember"
			}
		""";

	public static String testExistentCredentials2 = """
			{
				"email": "anna@books.com",
				"password": "annaadmin"
			}
		""";

	public static String testWrongCredentials = """
			{
				"email": "molly@books.com",
				"password": "MollyTHEMember"
			}
		""";

	public static String testNonExistentUser = """
			{
				"email": "kevindurant@gmail.com",
				"password": "mollymember"
			}
		""";

	public static String testNewUser = """
			{
				"name": "Kevin Durant",
				"email": "kevindurant@books.com",
				"password": "kevindurant"
			}
		""";

	public static String testNewlyCreatedUser = """
			{
				"email": "kevindurant@books.com",
				"password": "kevindurant"
			}
		""";
}
