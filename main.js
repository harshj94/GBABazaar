Parse.Cloud.afterSave ("Advertisement",function (request) {
	var GameScore = Parse.Object.extend("GameScore");
	var query = new Parse.Query(GameScore);
	query.get("ajCfInBYGZ", {
  		success: function(gameScore) {
    		gameScore.increment("score");
    		gameScore.save();
  		},
  		error: function(object, error) {
  		}
	});
});

Parse.Cloud.afterDelete ("Advertisement",function (request) {
  var GameScore = Parse.Object.extend("GameScore");
  var query = new Parse.Query(GameScore);
  query.get("ajCfInBYGZ", {
      success: function(gameScore) {
        gameScore.increment("score");
        gameScore.save();
      },
      error: function(object, error) {
      }
  });
});