package clast.seal.main;

import clast.seal.dao.UserDao;
import clast.seal.model.User;
import clast.seal.persistence.PersistenceManager;

public class Main {

	private static UserDao userDao;
	
	
	public static void main(String[] args) {
		
		PersistenceManager.setUpMySQLConnection("jdbc:mysql://localhost:3306/sealjpatestdb", "sealjpatestdb", "sealjpatestdb");

		userDao = new UserDao();
		
		userDao.createUser( new User("aaa", "g"));
		userDao.createUser( new User("ccc", "g"));
		userDao.createUser( new User("ss", "g"));
		userDao.createUser( new User("caddrla", "g"));
		
		System.out.println("Numero utenti: " + userDao.findAllUsers().size() );
		
		int i = 1;
		for(User user : userDao.findAllUsers() ) {
			System.out.println( i + ") " + user.getUsername() );
			i++;
		}
		
		PersistenceManager.tearDownDbConnection();
		
	}

}
