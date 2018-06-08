package clast.seal.mongo.service;

import clast.seal.mongo.persistence.Database;
import clast.seal.mongo.persistence.MongoDatabase;

public abstract class MongoService {
	
	Database db;
	
	MongoService() {
		db = new MongoDatabase();
	}
	
	void setDatabase(Database db) {
		this.db = db;
	}

}
