var db = db.getMongo().getDB("admin");
db.createUser({user:"hakan",pwd:"1234",roles:[{role:"root",db:"admin"}]});