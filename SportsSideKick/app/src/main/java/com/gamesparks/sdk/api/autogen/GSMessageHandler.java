package com.gamesparks.sdk.api.autogen;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gamesparks.sdk.api.AbstractMessage;
import com.gamesparks.sdk.api.GSData;
import com.gamesparks.sdk.GSEventConsumer;

import com.gamesparks.sdk.api.autogen.GSTypes.*;

public class GSMessageHandler {

	private GSEventConsumer<AchievementEarnedMessage> achievementEarnedMessageListener;
	
	public void setAchievementEarnedMessageListener(GSEventConsumer<AchievementEarnedMessage> achievementEarnedMessageListener)
	{
		this.achievementEarnedMessageListener = achievementEarnedMessageListener;
	}
	
	private GSEventConsumer<ChallengeAcceptedMessage> challengeAcceptedMessageListener;
	
	public void setChallengeAcceptedMessageListener(GSEventConsumer<ChallengeAcceptedMessage> challengeAcceptedMessageListener)
	{
		this.challengeAcceptedMessageListener = challengeAcceptedMessageListener;
	}
	
	private GSEventConsumer<ChallengeChangedMessage> challengeChangedMessageListener;
	
	public void setChallengeChangedMessageListener(GSEventConsumer<ChallengeChangedMessage> challengeChangedMessageListener)
	{
		this.challengeChangedMessageListener = challengeChangedMessageListener;
	}
	
	private GSEventConsumer<ChallengeChatMessage> challengeChatMessageListener;
	
	public void setChallengeChatMessageListener(GSEventConsumer<ChallengeChatMessage> challengeChatMessageListener)
	{
		this.challengeChatMessageListener = challengeChatMessageListener;
	}
	
	private GSEventConsumer<ChallengeDeclinedMessage> challengeDeclinedMessageListener;
	
	public void setChallengeDeclinedMessageListener(GSEventConsumer<ChallengeDeclinedMessage> challengeDeclinedMessageListener)
	{
		this.challengeDeclinedMessageListener = challengeDeclinedMessageListener;
	}
	
	private GSEventConsumer<ChallengeDrawnMessage> challengeDrawnMessageListener;
	
	public void setChallengeDrawnMessageListener(GSEventConsumer<ChallengeDrawnMessage> challengeDrawnMessageListener)
	{
		this.challengeDrawnMessageListener = challengeDrawnMessageListener;
	}
	
	private GSEventConsumer<ChallengeExpiredMessage> challengeExpiredMessageListener;
	
	public void setChallengeExpiredMessageListener(GSEventConsumer<ChallengeExpiredMessage> challengeExpiredMessageListener)
	{
		this.challengeExpiredMessageListener = challengeExpiredMessageListener;
	}
	
	private GSEventConsumer<ChallengeIssuedMessage> challengeIssuedMessageListener;
	
	public void setChallengeIssuedMessageListener(GSEventConsumer<ChallengeIssuedMessage> challengeIssuedMessageListener)
	{
		this.challengeIssuedMessageListener = challengeIssuedMessageListener;
	}
	
	private GSEventConsumer<ChallengeJoinedMessage> challengeJoinedMessageListener;
	
	public void setChallengeJoinedMessageListener(GSEventConsumer<ChallengeJoinedMessage> challengeJoinedMessageListener)
	{
		this.challengeJoinedMessageListener = challengeJoinedMessageListener;
	}
	
	private GSEventConsumer<ChallengeLapsedMessage> challengeLapsedMessageListener;
	
	public void setChallengeLapsedMessageListener(GSEventConsumer<ChallengeLapsedMessage> challengeLapsedMessageListener)
	{
		this.challengeLapsedMessageListener = challengeLapsedMessageListener;
	}
	
	private GSEventConsumer<ChallengeLostMessage> challengeLostMessageListener;
	
	public void setChallengeLostMessageListener(GSEventConsumer<ChallengeLostMessage> challengeLostMessageListener)
	{
		this.challengeLostMessageListener = challengeLostMessageListener;
	}
	
	private GSEventConsumer<ChallengeStartedMessage> challengeStartedMessageListener;
	
	public void setChallengeStartedMessageListener(GSEventConsumer<ChallengeStartedMessage> challengeStartedMessageListener)
	{
		this.challengeStartedMessageListener = challengeStartedMessageListener;
	}
	
	private GSEventConsumer<ChallengeTurnTakenMessage> challengeTurnTakenMessageListener;
	
	public void setChallengeTurnTakenMessageListener(GSEventConsumer<ChallengeTurnTakenMessage> challengeTurnTakenMessageListener)
	{
		this.challengeTurnTakenMessageListener = challengeTurnTakenMessageListener;
	}
	
	private GSEventConsumer<ChallengeWaitingMessage> challengeWaitingMessageListener;
	
	public void setChallengeWaitingMessageListener(GSEventConsumer<ChallengeWaitingMessage> challengeWaitingMessageListener)
	{
		this.challengeWaitingMessageListener = challengeWaitingMessageListener;
	}
	
	private GSEventConsumer<ChallengeWithdrawnMessage> challengeWithdrawnMessageListener;
	
	public void setChallengeWithdrawnMessageListener(GSEventConsumer<ChallengeWithdrawnMessage> challengeWithdrawnMessageListener)
	{
		this.challengeWithdrawnMessageListener = challengeWithdrawnMessageListener;
	}
	
	private GSEventConsumer<ChallengeWonMessage> challengeWonMessageListener;
	
	public void setChallengeWonMessageListener(GSEventConsumer<ChallengeWonMessage> challengeWonMessageListener)
	{
		this.challengeWonMessageListener = challengeWonMessageListener;
	}
	
	private GSEventConsumer<FriendMessage> friendMessageListener;
	
	public void setFriendMessageListener(GSEventConsumer<FriendMessage> friendMessageListener)
	{
		this.friendMessageListener = friendMessageListener;
	}
	
	private GSEventConsumer<GlobalRankChangedMessage> globalRankChangedMessageListener;
	
	public void setGlobalRankChangedMessageListener(GSEventConsumer<GlobalRankChangedMessage> globalRankChangedMessageListener)
	{
		this.globalRankChangedMessageListener = globalRankChangedMessageListener;
	}
	
	private GSEventConsumer<MatchFoundMessage> matchFoundMessageListener;
	
	public void setMatchFoundMessageListener(GSEventConsumer<MatchFoundMessage> matchFoundMessageListener)
	{
		this.matchFoundMessageListener = matchFoundMessageListener;
	}
	
	private GSEventConsumer<MatchNotFoundMessage> matchNotFoundMessageListener;
	
	public void setMatchNotFoundMessageListener(GSEventConsumer<MatchNotFoundMessage> matchNotFoundMessageListener)
	{
		this.matchNotFoundMessageListener = matchNotFoundMessageListener;
	}
	
	private GSEventConsumer<MatchUpdatedMessage> matchUpdatedMessageListener;
	
	public void setMatchUpdatedMessageListener(GSEventConsumer<MatchUpdatedMessage> matchUpdatedMessageListener)
	{
		this.matchUpdatedMessageListener = matchUpdatedMessageListener;
	}
	
	private GSEventConsumer<NewHighScoreMessage> newHighScoreMessageListener;
	
	public void setNewHighScoreMessageListener(GSEventConsumer<NewHighScoreMessage> newHighScoreMessageListener)
	{
		this.newHighScoreMessageListener = newHighScoreMessageListener;
	}
	
	private GSEventConsumer<NewTeamScoreMessage> newTeamScoreMessageListener;
	
	public void setNewTeamScoreMessageListener(GSEventConsumer<NewTeamScoreMessage> newTeamScoreMessageListener)
	{
		this.newTeamScoreMessageListener = newTeamScoreMessageListener;
	}
	
	private GSEventConsumer<ScriptMessage> scriptMessageListener;
	
	public void setScriptMessageListener(GSEventConsumer<ScriptMessage> scriptMessageListener)
	{
		this.scriptMessageListener = scriptMessageListener;
	}
	
	private GSEventConsumer<SessionTerminatedMessage> sessionTerminatedMessageListener;
	
	public void setSessionTerminatedMessageListener(GSEventConsumer<SessionTerminatedMessage> sessionTerminatedMessageListener)
	{
		this.sessionTerminatedMessageListener = sessionTerminatedMessageListener;
	}
	
	private GSEventConsumer<SocialRankChangedMessage> socialRankChangedMessageListener;
	
	public void setSocialRankChangedMessageListener(GSEventConsumer<SocialRankChangedMessage> socialRankChangedMessageListener)
	{
		this.socialRankChangedMessageListener = socialRankChangedMessageListener;
	}
	
	private GSEventConsumer<TeamChatMessage> teamChatMessageListener;
	
	public void setTeamChatMessageListener(GSEventConsumer<TeamChatMessage> teamChatMessageListener)
	{
		this.teamChatMessageListener = teamChatMessageListener;
	}
	
	private GSEventConsumer<TeamRankChangedMessage> teamRankChangedMessageListener;
	
	public void setTeamRankChangedMessageListener(GSEventConsumer<TeamRankChangedMessage> teamRankChangedMessageListener)
	{
		this.teamRankChangedMessageListener = teamRankChangedMessageListener;
	}
	
	private GSEventConsumer<UploadCompleteMessage> uploadCompleteMessageListener;
	
	public void setUploadCompleteMessageListener(GSEventConsumer<UploadCompleteMessage> uploadCompleteMessageListener)
	{
		this.uploadCompleteMessageListener = uploadCompleteMessageListener;
	}
	

	public void onMessageReceived(Map data){
		
		String type = data.get("@class").toString();
		
		if(false){}
		else if(type.equals(".AchievementEarnedMessage")){
			if(achievementEarnedMessageListener != null){
				achievementEarnedMessageListener.onEvent(new AchievementEarnedMessage(data));
			}
		}
		else if(type.equals(".ChallengeAcceptedMessage")){
			if(challengeAcceptedMessageListener != null){
				challengeAcceptedMessageListener.onEvent(new ChallengeAcceptedMessage(data));
			}
		}
		else if(type.equals(".ChallengeChangedMessage")){
			if(challengeChangedMessageListener != null){
				challengeChangedMessageListener.onEvent(new ChallengeChangedMessage(data));
			}
		}
		else if(type.equals(".ChallengeChatMessage")){
			if(challengeChatMessageListener != null){
				challengeChatMessageListener.onEvent(new ChallengeChatMessage(data));
			}
		}
		else if(type.equals(".ChallengeDeclinedMessage")){
			if(challengeDeclinedMessageListener != null){
				challengeDeclinedMessageListener.onEvent(new ChallengeDeclinedMessage(data));
			}
		}
		else if(type.equals(".ChallengeDrawnMessage")){
			if(challengeDrawnMessageListener != null){
				challengeDrawnMessageListener.onEvent(new ChallengeDrawnMessage(data));
			}
		}
		else if(type.equals(".ChallengeExpiredMessage")){
			if(challengeExpiredMessageListener != null){
				challengeExpiredMessageListener.onEvent(new ChallengeExpiredMessage(data));
			}
		}
		else if(type.equals(".ChallengeIssuedMessage")){
			if(challengeIssuedMessageListener != null){
				challengeIssuedMessageListener.onEvent(new ChallengeIssuedMessage(data));
			}
		}
		else if(type.equals(".ChallengeJoinedMessage")){
			if(challengeJoinedMessageListener != null){
				challengeJoinedMessageListener.onEvent(new ChallengeJoinedMessage(data));
			}
		}
		else if(type.equals(".ChallengeLapsedMessage")){
			if(challengeLapsedMessageListener != null){
				challengeLapsedMessageListener.onEvent(new ChallengeLapsedMessage(data));
			}
		}
		else if(type.equals(".ChallengeLostMessage")){
			if(challengeLostMessageListener != null){
				challengeLostMessageListener.onEvent(new ChallengeLostMessage(data));
			}
		}
		else if(type.equals(".ChallengeStartedMessage")){
			if(challengeStartedMessageListener != null){
				challengeStartedMessageListener.onEvent(new ChallengeStartedMessage(data));
			}
		}
		else if(type.equals(".ChallengeTurnTakenMessage")){
			if(challengeTurnTakenMessageListener != null){
				challengeTurnTakenMessageListener.onEvent(new ChallengeTurnTakenMessage(data));
			}
		}
		else if(type.equals(".ChallengeWaitingMessage")){
			if(challengeWaitingMessageListener != null){
				challengeWaitingMessageListener.onEvent(new ChallengeWaitingMessage(data));
			}
		}
		else if(type.equals(".ChallengeWithdrawnMessage")){
			if(challengeWithdrawnMessageListener != null){
				challengeWithdrawnMessageListener.onEvent(new ChallengeWithdrawnMessage(data));
			}
		}
		else if(type.equals(".ChallengeWonMessage")){
			if(challengeWonMessageListener != null){
				challengeWonMessageListener.onEvent(new ChallengeWonMessage(data));
			}
		}
		else if(type.equals(".FriendMessage")){
			if(friendMessageListener != null){
				friendMessageListener.onEvent(new FriendMessage(data));
			}
		}
		else if(type.equals(".GlobalRankChangedMessage")){
			if(globalRankChangedMessageListener != null){
				globalRankChangedMessageListener.onEvent(new GlobalRankChangedMessage(data));
			}
		}
		else if(type.equals(".MatchFoundMessage")){
			if(matchFoundMessageListener != null){
				matchFoundMessageListener.onEvent(new MatchFoundMessage(data));
			}
		}
		else if(type.equals(".MatchNotFoundMessage")){
			if(matchNotFoundMessageListener != null){
				matchNotFoundMessageListener.onEvent(new MatchNotFoundMessage(data));
			}
		}
		else if(type.equals(".MatchUpdatedMessage")){
			if(matchUpdatedMessageListener != null){
				matchUpdatedMessageListener.onEvent(new MatchUpdatedMessage(data));
			}
		}
		else if(type.equals(".NewHighScoreMessage")){
			if(newHighScoreMessageListener != null){
				newHighScoreMessageListener.onEvent(new NewHighScoreMessage(data));
			}
		}
		else if(type.equals(".NewTeamScoreMessage")){
			if(newTeamScoreMessageListener != null){
				newTeamScoreMessageListener.onEvent(new NewTeamScoreMessage(data));
			}
		}
		else if(type.equals(".ScriptMessage")){
			if(scriptMessageListener != null){
				scriptMessageListener.onEvent(new ScriptMessage(data));
			}
		}
		else if(type.equals(".SessionTerminatedMessage")){
			if(sessionTerminatedMessageListener != null){
				sessionTerminatedMessageListener.onEvent(new SessionTerminatedMessage(data));
			}
		}
		else if(type.equals(".SocialRankChangedMessage")){
			if(socialRankChangedMessageListener != null){
				socialRankChangedMessageListener.onEvent(new SocialRankChangedMessage(data));
			}
		}
		else if(type.equals(".TeamChatMessage")){
			if(teamChatMessageListener != null){
				teamChatMessageListener.onEvent(new TeamChatMessage(data));
			}
		}
		else if(type.equals(".TeamRankChangedMessage")){
			if(teamRankChangedMessageListener != null){
				teamRankChangedMessageListener.onEvent(new TeamRankChangedMessage(data));
			}
		}
		else if(type.equals(".UploadCompleteMessage")){
			if(uploadCompleteMessageListener != null){
				uploadCompleteMessageListener.onEvent(new UploadCompleteMessage(data));
			}
		}
	}
	
	
	public class AchievementEarnedMessage extends AbstractMessage{
		public AchievementEarnedMessage(Map data){
			super(data);
		}
			/** 
			* The name of achievement.
			*/ 
					public String getAchievementName(){
						return getString("achievementName");
					}
			/** 
			* The short code of the achievement.
			*/ 
					public String getAchievementShortCode(){
						return getString("achievementShortCode");
					}
			/** 
			* The amount of type 1 currency earned.
			*/ 
					public String getCurrency1Earned(){
						return getString("currency1Earned");
					}
			/** 
			* The amount of type 2 currency earned.
			*/ 
					public String getCurrency2Earned(){
						return getString("currency2Earned");
					}
			/** 
			* The amount of type 2 currency earned.
			*/ 
					public String getCurrency3Earned(){
						return getString("currency3Earned");
					}
			/** 
			* The amount of type 4 currency earned.
			*/ 
					public String getCurrency4Earned(){
						return getString("currency4Earned");
					}
			/** 
			* The amount of type 5 currency earned.
			*/ 
					public String getCurrency5Earned(){
						return getString("currency5Earned");
					}
			/** 
			* The amount of type 6 currency earned.
			*/ 
					public String getCurrency6Earned(){
						return getString("currency6Earned");
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
			/** 
			* The name of the virtual good that was earned.
			*/ 
					public String getVirtualGoodEarned(){
						return getString("virtualGoodEarned");
					}
	}
	
	
	public class ChallengeAcceptedMessage extends AbstractMessage{
		public ChallengeAcceptedMessage(Map data){
			super(data);
		}
			/** 
			* An object representing the challenge.
			*/ 
					public Challenge getChallenge(){
							if(getObject("challenge") == null) { return null; }
							return new Challenge(getObject("challenge"));
					}
			/** 
			* A player message included in this message.
			*/ 
					public String getMessage(){
						return getString("message");
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
			/** 
			* The name of the player whose actions generated this message.
			*/ 
					public String getWho(){
						return getString("who");
					}
	}
	
	
	public class ChallengeChangedMessage extends AbstractMessage{
		public ChallengeChangedMessage(Map data){
			super(data);
		}
			/** 
			* An object representing the challenge.
			*/ 
					public Challenge getChallenge(){
							if(getObject("challenge") == null) { return null; }
							return new Challenge(getObject("challenge"));
					}
			/** 
			* The leaderboard data associated with this challenge.
			*/ 
					public LeaderboardData getLeaderboardData(){
							if(getObject("leaderboardData") == null) { return null; }
							return new LeaderboardData(getObject("leaderboardData"));
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
			/** 
			* Indicates which player has changed the challenge
			*/ 
					public String getWho(){
						return getString("who");
					}
	}
	
	
	public class ChallengeChatMessage extends AbstractMessage{
		public ChallengeChatMessage(Map data){
			super(data);
		}
			/** 
			* An object representing the challenge.
			*/ 
					public Challenge getChallenge(){
							if(getObject("challenge") == null) { return null; }
							return new Challenge(getObject("challenge"));
					}
			/** 
			* A player message included in this message.
			*/ 
					public String getMessage(){
						return getString("message");
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
			/** 
			* The name of the player whose actions generated this message.
			*/ 
					public String getWho(){
						return getString("who");
					}
	}
	
	
	public class ChallengeDeclinedMessage extends AbstractMessage{
		public ChallengeDeclinedMessage(Map data){
			super(data);
		}
			/** 
			* An object representing the challenge.
			*/ 
					public Challenge getChallenge(){
							if(getObject("challenge") == null) { return null; }
							return new Challenge(getObject("challenge"));
					}
			/** 
			* A player message included in this message.
			*/ 
					public String getMessage(){
						return getString("message");
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
			/** 
			* The name of the player whose actions generated this message.
			*/ 
					public String getWho(){
						return getString("who");
					}
	}
	
	
	public class ChallengeDrawnMessage extends AbstractMessage{
		public ChallengeDrawnMessage(Map data){
			super(data);
		}
			/** 
			* An object representing the challenge.
			*/ 
					public Challenge getChallenge(){
							if(getObject("challenge") == null) { return null; }
							return new Challenge(getObject("challenge"));
					}
			/** 
			* The leaderboard data associated with this challenge.
			*/ 
					public LeaderboardData getLeaderboardData(){
							if(getObject("leaderboardData") == null) { return null; }
							return new LeaderboardData(getObject("leaderboardData"));
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
	}
	
	
	public class ChallengeExpiredMessage extends AbstractMessage{
		public ChallengeExpiredMessage(Map data){
			super(data);
		}
			/** 
			* An object representing the challenge.
			*/ 
					public Challenge getChallenge(){
							if(getObject("challenge") == null) { return null; }
							return new Challenge(getObject("challenge"));
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
	}
	
	
	public class ChallengeIssuedMessage extends AbstractMessage{
		public ChallengeIssuedMessage(Map data){
			super(data);
		}
			/** 
			* An object representing the challenge.
			*/ 
					public Challenge getChallenge(){
							if(getObject("challenge") == null) { return null; }
							return new Challenge(getObject("challenge"));
					}
			/** 
			* A player message included in this message.
			*/ 
					public String getMessage(){
						return getString("message");
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
			/** 
			* The name of the player whose actions generated this message.
			*/ 
					public String getWho(){
						return getString("who");
					}
	}
	
	
	public class ChallengeJoinedMessage extends AbstractMessage{
		public ChallengeJoinedMessage(Map data){
			super(data);
		}
			/** 
			* An object representing the challenge.
			*/ 
					public Challenge getChallenge(){
							if(getObject("challenge") == null) { return null; }
							return new Challenge(getObject("challenge"));
					}
			/** 
			* A player message included in this message.
			*/ 
					public String getMessage(){
						return getString("message");
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
			/** 
			* The name of the player whose actions generated this message.
			*/ 
					public String getWho(){
						return getString("who");
					}
	}
	
	
	public class ChallengeLapsedMessage extends AbstractMessage{
		public ChallengeLapsedMessage(Map data){
			super(data);
		}
			/** 
			* An object representing the challenge.
			*/ 
					public Challenge getChallenge(){
							if(getObject("challenge") == null) { return null; }
							return new Challenge(getObject("challenge"));
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
	}
	
	
	public class ChallengeLostMessage extends AbstractMessage{
		public ChallengeLostMessage(Map data){
			super(data);
		}
			/** 
			* An object representing the challenge.
			*/ 
					public Challenge getChallenge(){
							if(getObject("challenge") == null) { return null; }
							return new Challenge(getObject("challenge"));
					}
			/** 
			* The leaderboard data associated with this challenge.
			*/ 
					public LeaderboardData getLeaderboardData(){
							if(getObject("leaderboardData") == null) { return null; }
							return new LeaderboardData(getObject("leaderboardData"));
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
			/** 
			* The winning players name
			*/ 
					public String getWinnerName(){
						return getString("winnerName");
					}
	}
	
	
	public class ChallengeStartedMessage extends AbstractMessage{
		public ChallengeStartedMessage(Map data){
			super(data);
		}
			/** 
			* An object representing the challenge.
			*/ 
					public Challenge getChallenge(){
							if(getObject("challenge") == null) { return null; }
							return new Challenge(getObject("challenge"));
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
	}
	
	
	public class ChallengeTurnTakenMessage extends AbstractMessage{
		public ChallengeTurnTakenMessage(Map data){
			super(data);
		}
			/** 
			* An object representing the challenge.
			*/ 
					public Challenge getChallenge(){
							if(getObject("challenge") == null) { return null; }
							return new Challenge(getObject("challenge"));
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
			/** 
			* The name of the player whoe has taken their turn.
			*/ 
					public String getWho(){
						return getString("who");
					}
	}
	
	
	public class ChallengeWaitingMessage extends AbstractMessage{
		public ChallengeWaitingMessage(Map data){
			super(data);
		}
			/** 
			* An object representing the challenge.
			*/ 
					public Challenge getChallenge(){
							if(getObject("challenge") == null) { return null; }
							return new Challenge(getObject("challenge"));
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
	}
	
	
	public class ChallengeWithdrawnMessage extends AbstractMessage{
		public ChallengeWithdrawnMessage(Map data){
			super(data);
		}
			/** 
			* An object representing the challenge.
			*/ 
					public Challenge getChallenge(){
							if(getObject("challenge") == null) { return null; }
							return new Challenge(getObject("challenge"));
					}
			/** 
			* A player message included in this message.
			*/ 
					public String getMessage(){
						return getString("message");
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
			/** 
			* The name of the player whose actions generated this message.
			*/ 
					public String getWho(){
						return getString("who");
					}
	}
	
	
	public class ChallengeWonMessage extends AbstractMessage{
		public ChallengeWonMessage(Map data){
			super(data);
		}
			/** 
			* An object representing the challenge.
			*/ 
					public Challenge getChallenge(){
							if(getObject("challenge") == null) { return null; }
							return new Challenge(getObject("challenge"));
					}
			/** 
			* The amount of type 1 currency the player has won.
			*/ 
					public Long getCurrency1Won(){
						return getLong("currency1Won");
					}
			/** 
			* The amount of type 2 currency the player has won.
			*/ 
					public Long getCurrency2Won(){
						return getLong("currency2Won");
					}
			/** 
			* The amount of type 3 currency the player has won.
			*/ 
					public Long getCurrency3Won(){
						return getLong("currency3Won");
					}
			/** 
			* The amount of type 4 currency the player has won.
			*/ 
					public Long getCurrency4Won(){
						return getLong("currency4Won");
					}
			/** 
			* The amount of type 5 currency the player has won.
			*/ 
					public Long getCurrency5Won(){
						return getLong("currency5Won");
					}
			/** 
			* The amount of type 6 currency the player has won.
			*/ 
					public Long getCurrency6Won(){
						return getLong("currency6Won");
					}
			/** 
			* The leaderboard data associated with this challenge.
			*/ 
					public LeaderboardData getLeaderboardData(){
							if(getObject("leaderboardData") == null) { return null; }
							return new LeaderboardData(getObject("leaderboardData"));
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
			/** 
			* The winning player's name.
			*/ 
					public String getWinnerName(){
						return getString("winnerName");
					}
	}
	
	
	public class FriendMessage extends AbstractMessage{
		public FriendMessage(Map data){
			super(data);
		}
			/** 
			* The player's id who is sending the message.
			*/ 
					public String getFromId(){
						return getString("fromId");
					}
			/** 
			* The player's message.
			*/ 
					public String getMessage(){
						return getString("message");
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
			/** 
			* The name of the player who is sending the message.
			*/ 
					public String getWho(){
						return getString("who");
					}
	}
	
	
	public class GlobalRankChangedMessage extends AbstractMessage{
		public GlobalRankChangedMessage(Map data){
			super(data);
		}
			/** 
			* The game id that this message relates to.
			*/ 
					public Long getGameId(){
						return getLong("gameId");
					}
			/** 
			* The leaderboard's name.
			*/ 
					public String getLeaderboardName(){
						return getString("leaderboardName");
					}
			/** 
			* The leaderboard shortcode.
			*/ 
					public String getLeaderboardShortCode(){
						return getString("leaderboardShortCode");
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* The score details of the player whose score the receiving player has passed.
			*/ 
					public LeaderboardData getThem(){
							if(getObject("them") == null) { return null; }
							return new LeaderboardData(getObject("them"));
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
			/** 
			* The score details of the receiving player.
			*/ 
					public LeaderboardData getYou(){
							if(getObject("you") == null) { return null; }
							return new LeaderboardData(getObject("you"));
					}
	}
	
	
	public class MatchFoundMessage extends AbstractMessage{
		public MatchFoundMessage(Map data){
			super(data);
		}
			/** 
			* The accessToken used to authenticate this player for this match
			*/ 
					public String getAccessToken(){
						return getString("accessToken");
					}
			/** 
			* The host to connect to for this match
			*/ 
					public String getHost(){
						return getString("host");
					}
			/** 
			* MatchData is arbitrary data that can be stored in a Match instance by a Cloud Code script.
			*/ 
					public GSData getMatchData(){
						return getObject("matchData");
					}
			/** 
			* The group the player was assigned in the matchmaking request
			*/ 
					public String getMatchGroup(){
						return getString("matchGroup");
					}
			/** 
			* The id for this match instance
			*/ 
					public String getMatchId(){
						return getString("matchId");
					}
			/** 
			* The shortCode of the match type this message for
			*/ 
					public String getMatchShortCode(){
						return getString("matchShortCode");
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* The participants in this match
			*/ 
					public List<Participant> getParticipants(){
						return getWrapperObjectList("participants", Participant.class);
					}
			/** 
			* The port to connect to for this match
			*/ 
					public Integer getPort(){
						return getInteger("port");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
	}
	
	
	public class MatchNotFoundMessage extends AbstractMessage{
		public MatchNotFoundMessage(Map data){
			super(data);
		}
			/** 
			* MatchData is arbitrary data that can be stored in a Match instance by a Cloud Code script.
			*/ 
					public GSData getMatchData(){
						return getObject("matchData");
					}
			/** 
			* The group the player was assigned in the matchmaking request
			*/ 
					public String getMatchGroup(){
						return getString("matchGroup");
					}
			/** 
			* The shortCode of the match type this message for
			*/ 
					public String getMatchShortCode(){
						return getString("matchShortCode");
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A JSON Map of any data that was associated to this user
			*/ 
					public GSData getParticipantData(){
						return getObject("participantData");
					}
			/** 
			* The participants in this match
			*/ 
					public List<Participant> getParticipants(){
						return getWrapperObjectList("participants", Participant.class);
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
	}
	
	
	public class MatchUpdatedMessage extends AbstractMessage{
		public MatchUpdatedMessage(Map data){
			super(data);
		}
			/** 
			* The players that joined this match
			*/ 
						public List<String> getAddedPlayers(){
							return getStringList("addedPlayers");
						}
			/** 
			* MatchData is arbitrary data that can be stored in a Match instance by a Cloud Code script.
			*/ 
					public GSData getMatchData(){
						return getObject("matchData");
					}
			/** 
			* The group the player was assigned in the matchmaking request
			*/ 
					public String getMatchGroup(){
						return getString("matchGroup");
					}
			/** 
			* The id for this match instance
			*/ 
					public String getMatchId(){
						return getString("matchId");
					}
			/** 
			* The shortCode of the match type this message for
			*/ 
					public String getMatchShortCode(){
						return getString("matchShortCode");
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* The participants in this match
			*/ 
					public List<Participant> getParticipants(){
						return getWrapperObjectList("participants", Participant.class);
					}
			/** 
			* The players that left this match
			*/ 
						public List<String> getRemovedPlayers(){
							return getStringList("removedPlayers");
						}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
	}
	
	
	public class NewHighScoreMessage extends AbstractMessage{
		public NewHighScoreMessage(Map data){
			super(data);
		}
			/** 
			* The new leaderboard data associated with this message.
			*/ 
					public LeaderboardData getLeaderboardData(){
							if(getObject("leaderboardData") == null) { return null; }
							return new LeaderboardData(getObject("leaderboardData"));
					}
			/** 
			* The leaderboard's name.
			*/ 
					public String getLeaderboardName(){
						return getString("leaderboardName");
					}
			/** 
			* The leaderboard shortcode.
			*/ 
					public String getLeaderboardShortCode(){
						return getString("leaderboardShortCode");
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* The ranking information for the new score.
			*/ 
					public LeaderboardRankDetails getRankDetails(){
							if(getObject("rankDetails") == null) { return null; }
							return new LeaderboardRankDetails(getObject("rankDetails"));
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
	}
	
	
	public class NewTeamScoreMessage extends AbstractMessage{
		public NewTeamScoreMessage(Map data){
			super(data);
		}
			/** 
			* The new leaderboard data associated with this message.
			*/ 
					public LeaderboardData getLeaderboardData(){
							if(getObject("leaderboardData") == null) { return null; }
							return new LeaderboardData(getObject("leaderboardData"));
					}
			/** 
			* The leaderboard's name.
			*/ 
					public String getLeaderboardName(){
						return getString("leaderboardName");
					}
			/** 
			* The leaderboard shortcode.
			*/ 
					public String getLeaderboardShortCode(){
						return getString("leaderboardShortCode");
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* The ranking information for the new score.
			*/ 
					public LeaderboardRankDetails getRankDetails(){
							if(getObject("rankDetails") == null) { return null; }
							return new LeaderboardRankDetails(getObject("rankDetails"));
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
	}
	
	
	public class ScriptMessage extends AbstractMessage{
		public ScriptMessage(Map data){
			super(data);
		}
			/** 
			* JSON data sent from a Cloud Code script.
			*/ 
					public GSData getData(){
						return getObject("data");
					}
			/** 
			* The extension code used when creating this script message
			*/ 
					public String getExtCode(){
						return getString("extCode");
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
	}
	
	
	public class SessionTerminatedMessage extends AbstractMessage{
		public SessionTerminatedMessage(Map data){
			super(data);
		}
			/** 
			* Used to automatically re-authenticate a client during socket connect.
			*/ 
					public String getAuthToken(){
						return getString("authToken");
					}
	}
	
	
	public class SocialRankChangedMessage extends AbstractMessage{
		public SocialRankChangedMessage(Map data){
			super(data);
		}
			/** 
			* The game id that this message relates to.
			*/ 
					public Long getGameId(){
						return getLong("gameId");
					}
			/** 
			* The leaderboard's name.
			*/ 
					public String getLeaderboardName(){
						return getString("leaderboardName");
					}
			/** 
			* The leaderboard shortcode.
			*/ 
					public String getLeaderboardShortCode(){
						return getString("leaderboardShortCode");
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* The score details of the player whose score the receiving player has passed.
			*/ 
					public LeaderboardData getThem(){
							if(getObject("them") == null) { return null; }
							return new LeaderboardData(getObject("them"));
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
			/** 
			* The score details of the receiving player.
			*/ 
					public LeaderboardData getYou(){
							if(getObject("you") == null) { return null; }
							return new LeaderboardData(getObject("you"));
					}
	}
	
	
	public class TeamChatMessage extends AbstractMessage{
		public TeamChatMessage(Map data){
			super(data);
		}
			/** 
			* The identifier for this message as it appears in the chat history.
			*/ 
					public String getChatMessageId(){
						return getString("chatMessageId");
					}
			/** 
			* The player's id who is sending the message.
			*/ 
					public String getFromId(){
						return getString("fromId");
					}
			/** 
			* The message to send to the team.
			*/ 
					public String getMessage(){
						return getString("message");
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* The id of the owner
			*/ 
					public String getOwnerId(){
						return getString("ownerId");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* The id of the team
			*/ 
					public String getTeamId(){
						return getString("teamId");
					}
			/** 
			* The team type
			*/ 
					public String getTeamType(){
						return getString("teamType");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
			/** 
			* The name of the player who is sending the message.
			*/ 
					public String getWho(){
						return getString("who");
					}
	}
	
	
	public class TeamRankChangedMessage extends AbstractMessage{
		public TeamRankChangedMessage(Map data){
			super(data);
		}
			/** 
			* The game id that this message relates to.
			*/ 
					public Long getGameId(){
						return getLong("gameId");
					}
			/** 
			* The leaderboard's name.
			*/ 
					public String getLeaderboardName(){
						return getString("leaderboardName");
					}
			/** 
			* The leaderboard shortcode.
			*/ 
					public String getLeaderboardShortCode(){
						return getString("leaderboardShortCode");
					}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* The score details of the team whose score the receiving team's players have passed.
			*/ 
					public LeaderboardData getThem(){
							if(getObject("them") == null) { return null; }
							return new LeaderboardData(getObject("them"));
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
			/** 
			* The score details of the receiving team.
			*/ 
					public LeaderboardData getYou(){
							if(getObject("you") == null) { return null; }
							return new LeaderboardData(getObject("you"));
					}
	}
	
	
	public class UploadCompleteMessage extends AbstractMessage{
		public UploadCompleteMessage(Map data){
			super(data);
		}
			/** 
			* A unique identifier for this message.
			*/ 
					public String getMessageId(){
						return getString("messageId");
					}
			/** 
			* Flag indicating whether this message could be sent as a push notification or not.
			*/ 
					public Boolean getNotification(){
						return getBoolean("notification");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getSubTitle(){
						return getString("subTitle");
					}
			/** 
			* A textual summary describing the message's purpose.
			*/ 
					public String getSummary(){
						return getString("summary");
					}
			/** 
			* A textual title for the message.
			*/ 
					public String getTitle(){
						return getString("title");
					}
			/** 
			* The upload data (if supplied as part of GetUploadUrlRequest) of UploadData objects
			*/ 
					public UploadData getUploadData(){
							if(getObject("uploadData") == null) { return null; }
							return new UploadData(getObject("uploadData"));
					}
			/** 
			* The id of the upload
			*/ 
					public String getUploadId(){
						return getString("uploadId");
					}
	}
	
	
}

