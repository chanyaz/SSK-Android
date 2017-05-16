package com.gamesparks.sdk.api.autogen;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gamesparks.sdk.api.GSData;

public class GSTypes
{
	
	
	public static class BulkJob extends GSData {
		
		public BulkJob(){};
		
		public BulkJob(Map data){
			super(data);
		}
		
		public BulkJob(GSData data){
			super(data);
		}
			/** 
			* The actual count of players affected by the bulk job (calculated when the job started to run)
			*/ 
					public Long getActualCount(){
						return getLong("actualCount");
					}
			/** 
			* The time at which the bulk job completed execution
			*/ 
					public Date getCompleted(){
						return getDate("completed");
					}
			/** 
			* The time at which the bulk job was created
			*/ 
					public Date getCreated(){
						return getDate("created");
					}
			/** 
			* Data to be passed into the Module or Script
			*/ 
					public GSData getData(){
						return getObject("data");
					}
			/** 
			* The number of players processed by the bulk job so far
			*/ 
					public Long getDoneCount(){
						return getLong("doneCount");
					}
			/** 
			* The number of errors encountered whilst running the Module or Script for players
			*/ 
					public Long getErrorCount(){
						return getLong("errorCount");
					}
			/** 
			* The estimated count of players affected by the bulk job (estimated when the job was submitted)
			*/ 
					public Long getEstimatedCount(){
						return getLong("estimatedCount");
					}
			/** 
			* The ID for the bulk job
			*/ 
					public String getId(){
						return getString("id");
					}
			/** 
			* The Cloud Code Module to run for each player
			*/ 
					public String getModuleShortCode(){
						return getString("moduleShortCode");
					}
			/** 
			* The query to identify players to perform the bulk job on
			*/ 
					public GSData getPlayerQuery(){
						return getObject("playerQuery");
					}
			/** 
			* The time at which the job was scheduled to run
			*/ 
					public Date getScheduledTime(){
						return getDate("scheduledTime");
					}
			/** 
			* The Cloud Code script to run for each player
			*/ 
					public String getScript(){
						return getString("script");
					}
			/** 
			* The time at which the bulk job started to execute
			*/ 
					public Date getStarted(){
						return getDate("started");
					}
			/** 
			* The current state of the bulk job
			*/ 
					public String getState(){
						return getString("state");
					}
		
		
	}
	
	
	public static class InvitableFriend extends GSData {
		
		public InvitableFriend(){};
		
		public InvitableFriend(Map data){
			super(data);
		}
		
		public InvitableFriend(GSData data){
			super(data);
		}
			/** 
			* The display name of the External Friend
			*/ 
					public String getDisplayName(){
						return getString("displayName");
					}
			/** 
			* The ID of the External Friend
			*/ 
					public String getId(){
						return getString("id");
					}
			/** 
			* The profile picture URL of the External Friend
			*/ 
					public String getProfilePic(){
						return getString("profilePic");
					}
		
		
	}
	
	
	public static class Achievement extends GSData {
		
		public Achievement(){};
		
		public Achievement(Map data){
			super(data);
		}
		
		public Achievement(GSData data){
			super(data);
		}
			/** 
			* The desciption of the Achievement
			*/ 
					public String getDescription(){
						return getString("description");
					}
			/** 
			* Whether to current player has earned the achievement
			*/ 
					public Boolean getEarned(){
						return getBoolean("earned");
					}
			/** 
			* The name of the Achievement
			*/ 
					public String getName(){
						return getString("name");
					}
			/** 
			* The custom property set configured on this Achievement
			*/ 
					public GSData getPropertySet(){
						return getObject("propertySet");
					}
			/** 
			* The shortCode of the Achievement
			*/ 
					public String getShortCode(){
						return getString("shortCode");
					}
		
		
	}
	
	
	public static class BundledGood extends GSData {
		
		public BundledGood(){};
		
		public BundledGood(Map data){
			super(data);
		}
		
		public BundledGood(GSData data){
			super(data);
		}
			/** 
			* The number of items bundled
			*/ 
					public Integer getQty(){
						return getInteger("qty");
					}
			/** 
			* The shortCode of the bundled good
			*/ 
					public String getShortCode(){
						return getString("shortCode");
					}
		
		
	}
	
	
	public static class VirtualGood extends GSData {
		
		public VirtualGood(){};
		
		public VirtualGood(Map data){
			super(data);
		}
		
		public VirtualGood(GSData data){
			super(data);
		}
			/** 
			* The Windows Phone 8 productId of the item.
			*/ 
					public String getWP8StoreProductId(){
						return getString("WP8StoreProductId");
					}
			/** 
			* The Amazon Store productId of the item.
			*/ 
					public String getAmazonStoreProductId(){
						return getString("amazonStoreProductId");
					}
			/** 
			* The Base Currency1 cost of the Virtual Good, before segmentation
			*/ 
					public Long getBaseCurrency1Cost(){
						return getLong("baseCurrency1Cost");
					}
			/** 
			* The Base Currency2 cost of the Virtual Good, before segmentation
			*/ 
					public Long getBaseCurrency2Cost(){
						return getLong("baseCurrency2Cost");
					}
			/** 
			* The Base Currency3 cost of the Virtual Good, before segmentation
			*/ 
					public Long getBaseCurrency3Cost(){
						return getLong("baseCurrency3Cost");
					}
			/** 
			* The Base Currency4 cost of the Virtual Good, before segmentation
			*/ 
					public Long getBaseCurrency4Cost(){
						return getLong("baseCurrency4Cost");
					}
			/** 
			* The Base Currency5 cost of the Virtual Good, before segmentation
			*/ 
					public Long getBaseCurrency5Cost(){
						return getLong("baseCurrency5Cost");
					}
			/** 
			* The Base Currency6 cost of the Virtual Good, before segmentation
			*/ 
					public Long getBaseCurrency6Cost(){
						return getLong("baseCurrency6Cost");
					}
			/** 
			* Returns the bundle goods of the virtual good
			*/ 
					public List<BundledGood> getBundledGoods(){
						return getWrapperObjectList("bundledGoods", BundledGood.class);
					}
			/** 
			* The Currency1 cost of the Virtual Good
			*/ 
					public Long getCurrency1Cost(){
						return getLong("currency1Cost");
					}
			/** 
			* The Currency2 cost of the Virtual Good
			*/ 
					public Long getCurrency2Cost(){
						return getLong("currency2Cost");
					}
			/** 
			* The Currency3 cost of the Virtual Good
			*/ 
					public Long getCurrency3Cost(){
						return getLong("currency3Cost");
					}
			/** 
			* The Currency4 cost of the Virtual Good
			*/ 
					public Long getCurrency4Cost(){
						return getLong("currency4Cost");
					}
			/** 
			* The Currency5 cost of the Virtual Good
			*/ 
					public Long getCurrency5Cost(){
						return getLong("currency5Cost");
					}
			/** 
			* The Currency6 cost of the Virtual Good
			*/ 
					public Long getCurrency6Cost(){
						return getLong("currency6Cost");
					}
			/** 
			* The description of the Virtual Good
			*/ 
					public String getDescription(){
						return getString("description");
					}
			/** 
			* Whether the item is disabled.
			*/ 
					public Boolean getDisabled(){
						return getBoolean("disabled");
					}
			/** 
			* The google play productId of the item.
			*/ 
					public String getGooglePlayProductId(){
						return getString("googlePlayProductId");
					}
			/** 
			* The iOS AppStore productId of the item.
			*/ 
					public String getIosAppStoreProductId(){
						return getString("iosAppStoreProductId");
					}
			/** 
			* The maximum number of the Virtual Good that can be owned at one time
			*/ 
					public Long getMaxQuantity(){
						return getLong("maxQuantity");
					}
			/** 
			* The name of the Virtual Good
			*/ 
					public String getName(){
						return getString("name");
					}
			/** 
			* The custom property set configured on the item.
			*/ 
					public GSData getPropertySet(){
						return getObject("propertySet");
					}
			/** 
			* The PSN Store productId of the item.
			*/ 
					public String getPsnStoreProductId(){
						return getString("psnStoreProductId");
					}
			/** 
			* The Segmented Currency1 cost of the Virtual Good, before experiments
			*/ 
					public Long getSegmentedCurrency1Cost(){
						return getLong("segmentedCurrency1Cost");
					}
			/** 
			* The Segmented Currency2 cost of the Virtual Good, before experiments
			*/ 
					public Long getSegmentedCurrency2Cost(){
						return getLong("segmentedCurrency2Cost");
					}
			/** 
			* The Segmented Currency3 cost of the Virtual Good, before experiments
			*/ 
					public Long getSegmentedCurrency3Cost(){
						return getLong("segmentedCurrency3Cost");
					}
			/** 
			* The Segmented Currency4 cost of the Virtual Good, before experiments
			*/ 
					public Long getSegmentedCurrency4Cost(){
						return getLong("segmentedCurrency4Cost");
					}
			/** 
			* The Segmented Currency5 cost of the Virtual Good, before experiments
			*/ 
					public Long getSegmentedCurrency5Cost(){
						return getLong("segmentedCurrency5Cost");
					}
			/** 
			* The Segmented Currency6 cost of the Virtual Good, before experiments
			*/ 
					public Long getSegmentedCurrency6Cost(){
						return getLong("segmentedCurrency6Cost");
					}
			/** 
			* The short code of the Virtual Good
			*/ 
					public String getShortCode(){
						return getString("shortCode");
					}
			/** 
			* The Steam Store productId of the item.
			*/ 
					public String getSteamStoreProductId(){
						return getString("steamStoreProductId");
					}
			/** 
			* The tags of the Virtual Good
			*/ 
					public String getTags(){
						return getString("tags");
					}
			/** 
			* The type of the virtual good, "VGOOD" or "CURRENCY"
			*/ 
					public String getType(){
						return getString("type");
					}
			/** 
			* The Windows 8 productId of the item.
			*/ 
					public String getW8StoreProductId(){
						return getString("w8StoreProductId");
					}
		
		
	}
	
	
	public static class Player extends GSData {
		
		public Player(){};
		
		public Player(Map data){
			super(data);
		}
		
		public Player(GSData data){
			super(data);
		}
			/** 
			* The achievements of the Player
			*/ 
						public List<String> getAchievements(){
							return getStringList("achievements");
						}
			/** 
			* The display name of the Player
			*/ 
					public String getDisplayName(){
						return getString("displayName");
					}
			/** 
			* The external Id's of the Player
			*/ 
					public GSData getExternalIds(){
						return getObject("externalIds");
					}
			/** 
			* The id of the Player
			*/ 
					public String getId(){
						return getString("id");
					}
			/** 
			* The online status of the Player
			*/ 
					public Boolean getOnline(){
						return getBoolean("online");
					}
			/** 
			* The virtual goods of the Player
			*/ 
						public List<String> getVirtualGoods(){
							return getStringList("virtualGoods");
						}
		
		
	}
	
	
	public static class Team extends GSData {
		
		public Team(){};
		
		public Team(Map data){
			super(data);
		}
		
		public Team(GSData data){
			super(data);
		}
			/** 
			* The team members
			*/ 
					public List<Player> getMembers(){
						return getWrapperObjectList("members", Player.class);
					}
			/** 
			* A summary of the owner
			*/ 
					public Player getOwner(){
							if(getObject("owner") == null) { return null; }
							return new Player(getObject("owner"));
					}
			/** 
			* The Id of the team
			*/ 
					public String getTeamId(){
						return getString("teamId");
					}
			/** 
			* The team name
			*/ 
					public String getTeamName(){
						return getString("teamName");
					}
			/** 
			* The team type
			*/ 
					public String getTeamType(){
						return getString("teamType");
					}
		
		
	}
	
	
	public static class Leaderboard extends GSData {
		
		public Leaderboard(){};
		
		public Leaderboard(Map data){
			super(data);
		}
		
		public Leaderboard(GSData data){
			super(data);
		}
			/** 
			* The leaderboard's description.
			*/ 
					public String getDescription(){
						return getString("description");
					}
			/** 
			* The leaderboard's name.
			*/ 
					public String getName(){
						return getString("name");
					}
			/** 
			* The custom property set configured on this Leaderboard
			*/ 
					public GSData getPropertySet(){
						return getObject("propertySet");
					}
			/** 
			* The leaderboard's short code.
			*/ 
					public String getShortCode(){
						return getString("shortCode");
					}
		
		
	}
	
	
	public static class PlayerDetail extends GSData {
		
		public PlayerDetail(){};
		
		public PlayerDetail(Map data){
			super(data);
		}
		
		public PlayerDetail(GSData data){
			super(data);
		}
			/** 
			* A player's external identifiers
			*/ 
					public GSData getExternalIds(){
						return getObject("externalIds");
					}
			/** 
			* A player's id
			*/ 
					public String getId(){
						return getString("id");
					}
			/** 
			* A player's name
			*/ 
					public String getName(){
						return getString("name");
					}
		
		
	}
	
	
	public static class ChatMessage extends GSData {
		
		public ChatMessage(){};
		
		public ChatMessage(Map data){
			super(data);
		}
		
		public ChatMessage(GSData data){
			super(data);
		}
			/** 
			* The id of the player who sent this message
			*/ 
					public String getFromId(){
						return getString("fromId");
					}
			/** 
			* The id of this chat message
			*/ 
					public String getId(){
						return getString("id");
					}
			/** 
			* The text sent in this message
			*/ 
					public String getMessage(){
						return getString("message");
					}
			/** 
			* A date representing the time this message was sent
			*/ 
					public Date getWhen(){
						return getDate("when");
					}
			/** 
			* The displayName of the player who sent this message
			*/ 
					public String getWho(){
						return getString("who");
					}
		
		
	}
	
	
	public static class UploadData extends GSData {
		
		public UploadData(){};
		
		public UploadData(Map data){
			super(data);
		}
		
		public UploadData(GSData data){
			super(data);
		}
			/** 
			* The filename of the file that was uploaded.
			*/ 
					public String getFileName(){
						return getString("fileName");
					}
			/** 
			* The unique player id of the player that uploaded the file.
			*/ 
					public String getPlayerId(){
						return getString("playerId");
					}
		
		
	}
	
	
	public static class ChallengeType extends GSData {
		
		public ChallengeType(){};
		
		public ChallengeType(Map data){
			super(data);
		}
		
		public ChallengeType(GSData data){
			super(data);
		}
			/** 
			* The shortCode for this challenge.
			*/ 
					public String getChallengeShortCode(){
						return getString("challengeShortCode");
					}
			/** 
			* The description of this challenge.
			*/ 
					public String getDescription(){
						return getString("description");
					}
			/** 
			* The name of the leaderboard for this challenge.
			*/ 
					public String getGetleaderboardName(){
						return getString("getleaderboardName");
					}
			/** 
			* The name of this challenge.
			*/ 
					public String getName(){
						return getString("name");
					}
			/** 
			* The tags for this challenge.
			*/ 
					public String getTags(){
						return getString("tags");
					}
		
		
	}
	
	
	public static class Boughtitem extends GSData {
		
		public Boughtitem(){};
		
		public Boughtitem(Map data){
			super(data);
		}
		
		public Boughtitem(GSData data){
			super(data);
		}
			/** 
			* The quantity of the bought item
			*/ 
					public Long getQuantity(){
						return getLong("quantity");
					}
			/** 
			* The short code of the bought item
			*/ 
					public String getShortCode(){
						return getString("shortCode");
					}
		
		
	}
	
	
	public static class PlayerMessage extends GSData {
		
		public PlayerMessage(){};
		
		public PlayerMessage(Map data){
			super(data);
		}
		
		public PlayerMessage(GSData data){
			super(data);
		}
			/** 
			* The id of the message
			*/ 
					public String getId(){
						return getString("id");
					}
			/** 
			* The message content
			*/ 
					public GSData getMessage(){
						return getObject("message");
					}
			/** 
			* Whether the message has been delivered to the client
			*/ 
					public Boolean getSeen(){
						return getBoolean("seen");
					}
			/** 
			* The status of the message
			*/ 
					public String getStatus(){
						return getString("status");
					}
			/** 
			* The date of the message
			*/ 
					public Date getWhen(){
						return getDate("when");
					}
		
		
	}
	
	
	public static class MatchedPlayer extends GSData {
		
		public MatchedPlayer(){};
		
		public MatchedPlayer(Map data){
			super(data);
		}
		
		public MatchedPlayer(GSData data){
			super(data);
		}
			/** 
			* The Location of the player
			*/ 
					public GSData getLocation(){
						return getObject("location");
					}
			/** 
			* A JSON Map of any data that was associated to this user
			*/ 
					public GSData getParticipantData(){
						return getObject("participantData");
					}
			/** 
			* The ID for player
			*/ 
					public String getPlayerId(){
						return getString("playerId");
					}
			/** 
			* The skill of the player in this match
			*/ 
					public Double getSkill(){
						return getDouble("skill");
					}
		
		
	}
	
	
	public static class PendingMatch extends GSData {
		
		public PendingMatch(){};
		
		public PendingMatch(Map data){
			super(data);
		}
		
		public PendingMatch(GSData data){
			super(data);
		}
			/** 
			* The ID for the pending match
			*/ 
					public String getId(){
						return getString("id");
					}
			/** 
			* A JSON Map of the matchData associated to this pending match
			*/ 
					public GSData getMatchData(){
						return getObject("matchData");
					}
			/** 
			* The match group for the pending match
			*/ 
					public String getMatchGroup(){
						return getString("matchGroup");
					}
			/** 
			* The match shortCode for the pending match
			*/ 
					public String getMatchShortCode(){
						return getString("matchShortCode");
					}
			/** 
			* The players already part of this pending match
			*/ 
					public List<MatchedPlayer> getMatchedPlayers(){
						return getWrapperObjectList("matchedPlayers", MatchedPlayer.class);
					}
			/** 
			* The average skill of players in this pending match
			*/ 
					public Double getSkill(){
						return getDouble("skill");
					}
		
		
	}
	
	
	public static class Participant extends GSData {
		
		public Participant(){};
		
		public Participant(Map data){
			super(data);
		}
		
		public Participant(GSData data){
			super(data);
		}
			/** 
			* The achievements of the Player
			*/ 
						public List<String> getAchievements(){
							return getStringList("achievements");
						}
			/** 
			* The display name of the Player
			*/ 
					public String getDisplayName(){
						return getString("displayName");
					}
			/** 
			* The external Id's of the Player
			*/ 
					public GSData getExternalIds(){
						return getObject("externalIds");
					}
			/** 
			* The id of the Player
			*/ 
					public String getId(){
						return getString("id");
					}
			/** 
			* The online status of the Player
			*/ 
					public Boolean getOnline(){
						return getBoolean("online");
					}
			/** 
			* A JSON Map of any data that was associated to this user
			*/ 
					public GSData getParticipantData(){
						return getObject("participantData");
					}
			/** 
			* The peerId of this participant within the match
			*/ 
					public Integer getPeerId(){
						return getInteger("peerId");
					}
			/** 
			* The virtual goods of the Player
			*/ 
						public List<String> getVirtualGoods(){
							return getStringList("virtualGoods");
						}
		
		
	}
	
	
	public static class PlayerTransactionItem extends GSData {
		
		public PlayerTransactionItem(){};
		
		public PlayerTransactionItem(Map data){
			super(data);
		}
		
		public PlayerTransactionItem(GSData data){
			super(data);
		}
			/** 
			* The amount of this item given to the player in the transaction
			*/ 
					public Long getAmount(){
						return getLong("amount");
					}
			/** 
			* The quantity the player possesses after the transaction completed
			*/ 
					public Long getNewValue(){
						return getLong("newValue");
					}
			/** 
			* The type of item
			*/ 
					public String getType(){
						return getString("type");
					}
		
		
	}
	
	
	public static class PlayerTransaction extends GSData {
		
		public PlayerTransaction(){};
		
		public PlayerTransaction(Map data){
			super(data);
		}
		
		public PlayerTransaction(GSData data){
			super(data);
		}
			/** 
			* The items (currency or virtual goods) involved in this transaction
			*/ 
					public List<PlayerTransactionItem> getItems(){
						return getWrapperObjectList("items", PlayerTransactionItem.class);
					}
			/** 
			* The original request ID for this transaction
			*/ 
					public String getOriginalRequestId(){
						return getString("originalRequestId");
					}
			/** 
			* The player ID
			*/ 
					public String getPlayerId(){
						return getString("playerId");
					}
			/** 
			* The reason for the transaction
			*/ 
					public String getReason(){
						return getString("reason");
					}
			/** 
			* Gets the date when this transaction was revoked, if applicable
			*/ 
					public Date getRevokeDate(){
						return getDate("revokeDate");
					}
			/** 
			* Is true if the transaction was revoked
			*/ 
					public Boolean getRevoked(){
						return getBoolean("revoked");
					}
			/** 
			* The specific script in which this transaction occurred
			*/ 
					public String getScript(){
						return getString("script");
					}
			/** 
			* The script type in which this transaction occurred (e.g. event)
			*/ 
					public String getScriptType(){
						return getString("scriptType");
					}
			/** 
			* The transaction ID of this purchase, if applicable
			*/ 
					public String getTransactionId(){
						return getString("transactionId");
					}
			/** 
			* The date of the transaction
			*/ 
					public Date getWhen(){
						return getDate("when");
					}
		
		
	}
	
	
	public static class PlayerTurnCount extends GSData {
		
		public PlayerTurnCount(){};
		
		public PlayerTurnCount(Map data){
			super(data);
		}
		
		public PlayerTurnCount(GSData data){
			super(data);
		}
			/** 
			* The number of turns that the player has taken so far during this challenge.
			*/ 
					public String getCount(){
						return getString("count");
					}
			/** 
			* The unique player id.
			*/ 
					public String getPlayerId(){
						return getString("playerId");
					}
		
		
	}
	
	
	public static class Challenge extends GSData {
		
		public Challenge(){};
		
		public Challenge(Map data){
			super(data);
		}
		
		public Challenge(GSData data){
			super(data);
		}
			/** 
			* A list of PlayerDetail objects that represents the players that have accepted this challenge.
			*/ 
					public List<PlayerDetail> getAccepted(){
						return getWrapperObjectList("accepted", PlayerDetail.class);
					}
			/** 
			* A unique identifier for this challenge.
			*/ 
					public String getChallengeId(){
						return getString("challengeId");
					}
			/** 
			* The message included in the challenge by the challenging player who created the challenge.
			*/ 
					public String getChallengeMessage(){
						return getString("challengeMessage");
					}
			/** 
			* The name of the challenge that this message relates to.
			*/ 
					public String getChallengeName(){
						return getString("challengeName");
					}
			/** 
			* A list of PlayerDetail objects that represents the players that were challenged in this challenge.
			*/ 
					public List<PlayerDetail> getChallenged(){
						return getWrapperObjectList("challenged", PlayerDetail.class);
					}
			/** 
			* Details of the player who issued this challenge.
			*/ 
					public PlayerDetail getChallenger(){
							if(getObject("challenger") == null) { return null; }
							return new PlayerDetail(getObject("challenger"));
					}
			/** 
			* The amount of type 1 currency that has been wagered on this challenge.
			*/ 
					public Long getCurrency1Wager(){
						return getLong("currency1Wager");
					}
			/** 
			* The amount of type 2 currency that has been wagered on this challenge.
			*/ 
					public Long getCurrency2Wager(){
						return getLong("currency2Wager");
					}
			/** 
			* The amount of type 3 currency that has been wagered on this challenge.
			*/ 
					public Long getCurrency3Wager(){
						return getLong("currency3Wager");
					}
			/** 
			* The amount of type 4 currency that has been wagered on this challenge.
			*/ 
					public Long getCurrency4Wager(){
						return getLong("currency4Wager");
					}
			/** 
			* The amount of type 5 currency that has been wagered on this challenge.
			*/ 
					public Long getCurrency5Wager(){
						return getLong("currency5Wager");
					}
			/** 
			* The amount of type 6 currency that has been wagered on this challenge.
			*/ 
					public Long getCurrency6Wager(){
						return getLong("currency6Wager");
					}
			/** 
			* A list of PlayerDetail objects that represents the players that have declined this challenge.
			*/ 
					public List<PlayerDetail> getDeclined(){
						return getWrapperObjectList("declined", PlayerDetail.class);
					}
			/** 
			* The date when the challenge ends.
			*/ 
					public Date getEndDate(){
						return getDate("endDate");
					}
			/** 
			* The latest date that a player can accept the challenge.
			*/ 
					public Date getExpiryDate(){
						return getDate("expiryDate");
					}
			/** 
			* The maximum number of turns that this challenge is configured for.
			*/ 
					public Long getMaxTurns(){
						return getLong("maxTurns");
					}
			/** 
			* In a turn based challenge this fields contains the player's id whose turn it is next.
			*/ 
					public String getNextPlayer(){
						return getString("nextPlayer");
					}
			/** 
			* The challenge's short code.
			*/ 
					public String getShortCode(){
						return getString("shortCode");
					}
			/** 
			* The date when the challenge starts.
			*/ 
					public Date getStartDate(){
						return getDate("startDate");
					}
			/** 
			* One of these possible state values: ISSUED, EXPIRED, ACCEPTED, DECLINED, COMPLETE, WITHDRAWN, RUNNING, WAITING, RECEIVED
			*/ 
					public String getState(){
						return getString("state");
					}
			/** 
			* A collection containing the number of turns taken by each player that has accepted the challenge.
			* Each turn count is a Long keyed on a String that represents the player's id
			*/ 
					public List<PlayerTurnCount> getTurnCount(){
						return getWrapperObjectList("turnCount", PlayerTurnCount.class);
					}
		
		
	}
	
	
	public static class LeaderboardData extends GSData {
		
		public LeaderboardData(){};
		
		public LeaderboardData(Map data){
			super(data);
		}
		
		public LeaderboardData(GSData data){
			super(data);
		}
			/** 
			* The city where the player was located when they logged this leaderboard entry.
			*/ 
					public String getCity(){
						return getString("city");
					}
			/** 
			* The country code where the player was located when they logged this leaderboard entry.
			*/ 
					public String getCountry(){
						return getString("country");
					}
			/** 
			* The players rank.
			*/ 
					public GSData getExternalIds(){
						return getObject("externalIds");
					}
			/** 
			* The players rank.
			*/ 
					public Long getRank(){
						return getLong("rank");
					}
			/** 
			* The unique player id for this leaderboard entry.
			*/ 
					public String getUserId(){
						return getString("userId");
					}
			/** 
			* The players display name.
			*/ 
					public String getUserName(){
						return getString("userName");
					}
			/** 
			* The date when this leaderboard entry was created.
			*/ 
					public String getWhen(){
						return getString("when");
					}
		
		public Long GetNumberValue(String key){
			return getLong(key);
		}
		
		public String GetStringValue(String key){
			return getString(key);
		}
		
	}
	
	
	public static class LeaderboardRankDetails extends GSData {
		
		public LeaderboardRankDetails(){};
		
		public LeaderboardRankDetails(Map data){
			super(data);
		}
		
		public LeaderboardRankDetails(GSData data){
			super(data);
		}
			/** 
			* The leaderboard entries of the players friends that were beaten as part of this score submission.
			*/ 
					public List<LeaderboardData> getFriendsPassed(){
						return getWrapperObjectList("friendsPassed", LeaderboardData.class);
					}
			/** 
			* The number of entries in this leaderboard.
			*/ 
					public Long getGlobalCount(){
						return getLong("globalCount");
					}
			/** 
			* The Global Rank of the player in this leaderboard before the score was submitted.
			*/ 
					public Long getGlobalFrom(){
						return getLong("globalFrom");
					}
			/** 
			* The old global rank of the player as a percentage of the total number of scores in this leaderboard .
			*/ 
					public Long getGlobalFromPercent(){
						return getLong("globalFromPercent");
					}
			/** 
			* The Global Rank of the player in this leaderboard after the score was submitted.
			*/ 
					public Long getGlobalTo(){
						return getLong("globalTo");
					}
			/** 
			* The new global rank of the player as a percentage of the total number of scores in this leaderboard .
			*/ 
					public Long getGlobalToPercent(){
						return getLong("globalToPercent");
					}
			/** 
			* The number of friend entries the player has in this leaderboard.
			*/ 
					public Long getSocialCount(){
						return getLong("socialCount");
					}
			/** 
			* The Social Rank of the player in this leaderboard before the score was submitted.
			*/ 
					public Long getSocialFrom(){
						return getLong("socialFrom");
					}
			/** 
			* The old social rank of the player as a percentage of the total number of friend scores in this leaderboard.
			*/ 
					public Long getSocialFromPercent(){
						return getLong("socialFromPercent");
					}
			/** 
			* The Social Rank of the player in this leaderboard after the score was submitted.
			*/ 
					public Long getSocialTo(){
						return getLong("socialTo");
					}
			/** 
			* The old global rank of the player as a percentage of the total number of friend scores in this leaderboard.
			*/ 
					public Long getSocialToPercent(){
						return getLong("socialToPercent");
					}
			/** 
			* The leaderboard entries of the global players that were beaten as part of this score submission. Requires Top N to be configured on the leaderboard
			*/ 
					public List<LeaderboardData> getTopNPassed(){
						return getWrapperObjectList("topNPassed", LeaderboardData.class);
					}
		
		
	}
	
	
	public static class Location extends GSData {
		
		public Location(){};
		
		public Location(Map data){
			super(data);
		}
		
		public Location(GSData data){
			super(data);
		}
			/** 
			* The city
			*/ 
					public String getCity(){
						return getString("city");
					}
			/** 
			* The country
			*/ 
					public String getCountry(){
						return getString("country");
					}
			/** 
			* The latitude
			*/ 
					public Float getLatitide(){
						return getFloat("latitide");
					}
			/** 
			* The longditute
			*/ 
					public Float getLongditute(){
						return getFloat("longditute");
					}
		
		
	}
	
	
	public static class SocialStatus extends GSData {
		
		public SocialStatus(){};
		
		public SocialStatus(Map data){
			super(data);
		}
		
		public SocialStatus(GSData data){
			super(data);
		}
			/** 
			* When the token is still active.
			*/ 
					public Boolean getActive(){
						return getBoolean("active");
					}
			/** 
			* When the token expires (if available).
			*/ 
					public Date getExpires(){
						return getDate("expires");
					}
			/** 
			* The identifier of the external platform.
			*/ 
					public String getSystemId(){
						return getString("systemId");
					}
		
		
	}
	
	
}

