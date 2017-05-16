package com.gamesparks.sdk.api.autogen;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gamesparks.sdk.api.AbstractResponse;
import com.gamesparks.sdk.api.GSData;

import com.gamesparks.sdk.api.autogen.GSTypes.*;

public class GSResponseBuilder
{
	
	
	AcceptChallengeResponse newAcceptChallengeResponse(Map data){
		return new AcceptChallengeResponse(data);
	}
	
	public class AcceptChallengeResponse extends AbstractResponse{
		public AcceptChallengeResponse(Map data){
			super(data);
		}
	
			/** 
			* The ID of the challenge
			*/ 
					public String getChallengeInstanceId(){
						return getString("challengeInstanceId");
					}
	}
	
	
	AccountDetailsResponse newAccountDetailsResponse(Map data){
		return new AccountDetailsResponse(data);
	}
	
	public class AccountDetailsResponse extends AbstractResponse{
		public AccountDetailsResponse(Map data){
			super(data);
		}
	
			/** 
			* A JSON object containing the player's achievments
			*/ 
						public List<String> getAchievements(){
							return getStringList("achievements");
						}
			/** 
			* The amount of type 1 currency that the player holds
			*/ 
					public Long getCurrency1(){
						return getLong("currency1");
					}
			/** 
			* The amount of type 2 currency that the player holds
			*/ 
					public Long getCurrency2(){
						return getLong("currency2");
					}
			/** 
			* The amount of type 3 currency that the player holds
			*/ 
					public Long getCurrency3(){
						return getLong("currency3");
					}
			/** 
			* The amount of type 4 currency that the player holds
			*/ 
					public Long getCurrency4(){
						return getLong("currency4");
					}
			/** 
			* The amount of type 5 currency that the player holds
			*/ 
					public Long getCurrency5(){
						return getLong("currency5");
					}
			/** 
			* The amount of type 6 currency that the player holds
			*/ 
					public Long getCurrency6(){
						return getLong("currency6");
					}
			/** 
			* The player's display name
			*/ 
					public String getDisplayName(){
						return getString("displayName");
					}
			/** 
			* A JSON object containing the player's external account details
			*/ 
					public GSData getExternalIds(){
						return getObject("externalIds");
					}
			/** 
			* A JSON object containing the player's location
			*/ 
					public Location getLocation(){
							if(getObject("location") == null) { return null; }
							return new Location(getObject("location"));
					}
			/** 
			* The amount of type 1 currency that the player holds which is currently reserved
			*/ 
					public GSData getReservedCurrency1(){
						return getObject("reservedCurrency1");
					}
			/** 
			* The amount of type 2 currency that the player holds which is currently reserved
			*/ 
					public GSData getReservedCurrency2(){
						return getObject("reservedCurrency2");
					}
			/** 
			* The amount of type 3 currency that the player holds which is currently reserved
			*/ 
					public GSData getReservedCurrency3(){
						return getObject("reservedCurrency3");
					}
			/** 
			* The amount of type 4 currency that the player holds which is currently reserved
			*/ 
					public GSData getReservedCurrency4(){
						return getObject("reservedCurrency4");
					}
			/** 
			* The amount of type 5 currency that the player holds which is currently reserved
			*/ 
					public GSData getReservedCurrency5(){
						return getObject("reservedCurrency5");
					}
			/** 
			* The amount of type 6 currency that the player holds which is currently reserved
			*/ 
					public GSData getReservedCurrency6(){
						return getObject("reservedCurrency6");
					}
			/** 
			* The player's id
			*/ 
					public String getUserId(){
						return getString("userId");
					}
			/** 
			* A JSON object containing the virtual goods that the player owns
			*/ 
					public GSData getVirtualGoods(){
						return getObject("virtualGoods");
					}
	}
	
	
	AnalyticsResponse newAnalyticsResponse(Map data){
		return new AnalyticsResponse(data);
	}
	
	public class AnalyticsResponse extends AbstractResponse{
		public AnalyticsResponse(Map data){
			super(data);
		}
	
	}
	
	
	AroundMeLeaderboardResponse newAroundMeLeaderboardResponse(Map data){
		return new AroundMeLeaderboardResponse(data);
	}
	
	public class AroundMeLeaderboardResponse extends AbstractResponse{
		public AroundMeLeaderboardResponse(Map data){
			super(data);
		}
	
			/** 
			* The leaderboard's challenge id
			*/ 
					public String getChallengeInstanceId(){
						return getString("challengeInstanceId");
					}
			/** 
			* The leaderboard data
			*/ 
					public List<LeaderboardData> getData(){
						return getWrapperObjectList("data", LeaderboardData.class);
					}
			/** 
			* The first item in the leaderboard data
			*/ 
					public List<LeaderboardData> getFirst(){
						return getWrapperObjectList("first", LeaderboardData.class);
					}
			/** 
			* The last item in the leaderboard data
			*/ 
					public List<LeaderboardData> getLast(){
						return getWrapperObjectList("last", LeaderboardData.class);
					}
			/** 
			* The leaderboard short code
			*/ 
					public String getLeaderboardShortCode(){
						return getString("leaderboardShortCode");
					}
			/** 
			* True if the response contains a social leaderboard's data
			*/ 
					public Boolean getSocial(){
						return getBoolean("social");
					}
	}
	
	
	AuthenticationResponse newAuthenticationResponse(Map data){
		return new AuthenticationResponse(data);
	}
	
	public class AuthenticationResponse extends AbstractResponse{
		public AuthenticationResponse(Map data){
			super(data);
		}
	
			/** 
			* 44b297a8-162a-4220-8c14-dad9a1946ad2
			*/ 
					public String getAuthToken(){
						return getString("authToken");
					}
			/** 
			* The player's display name
			*/ 
					public String getDisplayName(){
						return getString("displayName");
					}
			/** 
			* Indicates whether the player was created as part of this request
			*/ 
					public Boolean getNewPlayer(){
						return getBoolean("newPlayer");
					}
			/** 
			* A summary of the player that would be switched to.  Only returned as part of an error response for a request where automatic switching is disabled.
			*/ 
					public Player getSwitchSummary(){
							if(getObject("switchSummary") == null) { return null; }
							return new Player(getObject("switchSummary"));
					}
			/** 
			* The player's id
			*/ 
					public String getUserId(){
						return getString("userId");
					}
	}
	
	
	BatchAdminResponse newBatchAdminResponse(Map data){
		return new BatchAdminResponse(data);
	}
	
	public class BatchAdminResponse extends AbstractResponse{
		public BatchAdminResponse(Map data){
			super(data);
		}
	
			/** 
			* A map of responses by player ID
			*/ 
					public GSData getResponses(){
						return getObject("responses");
					}
	}
	
	
	BuyVirtualGoodResponse newBuyVirtualGoodResponse(Map data){
		return new BuyVirtualGoodResponse(data);
	}
	
	public class BuyVirtualGoodResponse extends AbstractResponse{
		public BuyVirtualGoodResponse(Map data){
			super(data);
		}
	
			/** 
			* A JSON object containing details of the bought items
			*/ 
					public List<Boughtitem> getBoughtItems(){
						return getWrapperObjectList("boughtItems", Boughtitem.class);
					}
			/** 
			* How much currency type 1 was added
			*/ 
					public Long getCurrency1Added(){
						return getLong("currency1Added");
					}
			/** 
			* How much currency type 2 was added
			*/ 
					public Long getCurrency2Added(){
						return getLong("currency2Added");
					}
			/** 
			* How much currency type 3 was added
			*/ 
					public Long getCurrency3Added(){
						return getLong("currency3Added");
					}
			/** 
			* How much currency type 4 was added
			*/ 
					public Long getCurrency4Added(){
						return getLong("currency4Added");
					}
			/** 
			* How much currency type 5 was added
			*/ 
					public Long getCurrency5Added(){
						return getLong("currency5Added");
					}
			/** 
			* How much currency type 6 was added
			*/ 
					public Long getCurrency6Added(){
						return getLong("currency6Added");
					}
			/** 
			* For a buy with currency request, how much currency was used
			*/ 
					public Long getCurrencyConsumed(){
						return getLong("currencyConsumed");
					}
			/** 
			* For a buy with currency request, which currency type was used
			*/ 
					public Integer getCurrencyType(){
						return getInteger("currencyType");
					}
			/** 
			* A list of invalid items for this purchase (if any). This field is populated only for store buys
			*/ 
						public List<String> getInvalidItems(){
							return getStringList("invalidItems");
						}
			/** 
			* The list of transactionIds, for this purchase, if they exist. This field is populated only for store buys
			*/ 
						public List<String> getTransactionIds(){
							return getStringList("transactionIds");
						}
	}
	
	
	CancelBulkJobAdminResponse newCancelBulkJobAdminResponse(Map data){
		return new CancelBulkJobAdminResponse(data);
	}
	
	public class CancelBulkJobAdminResponse extends AbstractResponse{
		public CancelBulkJobAdminResponse(Map data){
			super(data);
		}
	
			/** 
			* A list of JSON objects containing bulk jobs
			*/ 
					public List<BulkJob> getBulkJobs(){
						return getWrapperObjectList("bulkJobs", BulkJob.class);
					}
	}
	
	
	ChangeUserDetailsResponse newChangeUserDetailsResponse(Map data){
		return new ChangeUserDetailsResponse(data);
	}
	
	public class ChangeUserDetailsResponse extends AbstractResponse{
		public ChangeUserDetailsResponse(Map data){
			super(data);
		}
	
	}
	
	
	ChatOnChallengeResponse newChatOnChallengeResponse(Map data){
		return new ChatOnChallengeResponse(data);
	}
	
	public class ChatOnChallengeResponse extends AbstractResponse{
		public ChatOnChallengeResponse(Map data){
			super(data);
		}
	
			/** 
			* The challenge instance id
			*/ 
					public String getChallengeInstanceId(){
						return getString("challengeInstanceId");
					}
	}
	
	
	ConsumeVirtualGoodResponse newConsumeVirtualGoodResponse(Map data){
		return new ConsumeVirtualGoodResponse(data);
	}
	
	public class ConsumeVirtualGoodResponse extends AbstractResponse{
		public ConsumeVirtualGoodResponse(Map data){
			super(data);
		}
	
	}
	
	
	CreateChallengeResponse newCreateChallengeResponse(Map data){
		return new CreateChallengeResponse(data);
	}
	
	public class CreateChallengeResponse extends AbstractResponse{
		public CreateChallengeResponse(Map data){
			super(data);
		}
	
			/** 
			* The challenge instance id
			*/ 
					public String getChallengeInstanceId(){
						return getString("challengeInstanceId");
					}
	}
	
	
	CreateTeamResponse newCreateTeamResponse(Map data){
		return new CreateTeamResponse(data);
	}
	
	public class CreateTeamResponse extends AbstractResponse{
		public CreateTeamResponse(Map data){
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
	
	
	DeclineChallengeResponse newDeclineChallengeResponse(Map data){
		return new DeclineChallengeResponse(data);
	}
	
	public class DeclineChallengeResponse extends AbstractResponse{
		public DeclineChallengeResponse(Map data){
			super(data);
		}
	
			/** 
			* The challenge instance id
			*/ 
					public String getChallengeInstanceId(){
						return getString("challengeInstanceId");
					}
	}
	
	
	DismissMessageResponse newDismissMessageResponse(Map data){
		return new DismissMessageResponse(data);
	}
	
	public class DismissMessageResponse extends AbstractResponse{
		public DismissMessageResponse(Map data){
			super(data);
		}
	
	}
	
	
	DismissMultipleMessagesResponse newDismissMultipleMessagesResponse(Map data){
		return new DismissMultipleMessagesResponse(data);
	}
	
	public class DismissMultipleMessagesResponse extends AbstractResponse{
		public DismissMultipleMessagesResponse(Map data){
			super(data);
		}
	
			/** 
			* A list of the messageId values that were not dismissed
			*/ 
					public List<String> getFailedDismissals(){
						return getStringList("failedDismissals");
					}
			/** 
			* An integer describing how many messages were dismissed
			*/ 
					public Integer getMessagesDismissed(){
						return getInteger("messagesDismissed");
					}
	}
	
	
	DropTeamResponse newDropTeamResponse(Map data){
		return new DropTeamResponse(data);
	}
	
	public class DropTeamResponse extends AbstractResponse{
		public DropTeamResponse(Map data){
			super(data);
		}
	
	}
	
	
	EndSessionResponse newEndSessionResponse(Map data){
		return new EndSessionResponse(data);
	}
	
	public class EndSessionResponse extends AbstractResponse{
		public EndSessionResponse(Map data){
			super(data);
		}
	
			/** 
			* The length of this session
			*/ 
					public Long getSessionDuration(){
						return getLong("sessionDuration");
					}
	}
	
	
	FindChallengeResponse newFindChallengeResponse(Map data){
		return new FindChallengeResponse(data);
	}
	
	public class FindChallengeResponse extends AbstractResponse{
		public FindChallengeResponse(Map data){
			super(data);
		}
	
			/** 
			* A list of JSON objects representing the challenges.
			*/ 
					public List<Challenge> getChallengeInstances(){
						return getWrapperObjectList("challengeInstances", Challenge.class);
					}
	}
	
	
	FindMatchResponse newFindMatchResponse(Map data){
		return new FindMatchResponse(data);
	}
	
	public class FindMatchResponse extends AbstractResponse{
		public FindMatchResponse(Map data){
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
			* The id for this match instance
			*/ 
					public String getMatchId(){
						return getString("matchId");
					}
			/** 
			* The opponents this player has been matched against
			*/ 
					public List<Player> getOpponents(){
						return getWrapperObjectList("opponents", Player.class);
					}
			/** 
			* The peerId of this player within the match
			*/ 
					public Integer getPeerId(){
						return getInteger("peerId");
					}
			/** 
			* The id of the current player
			*/ 
					public String getPlayerId(){
						return getString("playerId");
					}
			/** 
			* The port to connect to for this match
			*/ 
					public Integer getPort(){
						return getInteger("port");
					}
	}
	
	
	FindPendingMatchesResponse newFindPendingMatchesResponse(Map data){
		return new FindPendingMatchesResponse(data);
	}
	
	public class FindPendingMatchesResponse extends AbstractResponse{
		public FindPendingMatchesResponse(Map data){
			super(data);
		}
	
			/** 
			* A list of JSON objects containing pending matches
			*/ 
					public List<PendingMatch> getPendingMatches(){
						return getWrapperObjectList("pendingMatches", PendingMatch.class);
					}
	}
	
	
	GameSparksErrorResponse newGameSparksErrorResponse(Map data){
		return new GameSparksErrorResponse(data);
	}
	
	public class GameSparksErrorResponse extends AbstractResponse{
		public GameSparksErrorResponse(Map data){
			super(data);
		}
	
	}
	
	
	GetChallengeResponse newGetChallengeResponse(Map data){
		return new GetChallengeResponse(data);
	}
	
	public class GetChallengeResponse extends AbstractResponse{
		public GetChallengeResponse(Map data){
			super(data);
		}
	
			/** 
			* A JSON object representing the challenge.
			*/ 
					public Challenge getChallenge(){
							if(getObject("challenge") == null) { return null; }
							return new Challenge(getObject("challenge"));
					}
	}
	
	
	GetDownloadableResponse newGetDownloadableResponse(Map data){
		return new GetDownloadableResponse(data);
	}
	
	public class GetDownloadableResponse extends AbstractResponse{
		public GetDownloadableResponse(Map data){
			super(data);
		}
	
			/** 
			* The date when the downloadable item was last modified
			*/ 
					public Date getLastModified(){
						return getDate("lastModified");
					}
			/** 
			* The short code of the item
			*/ 
					public String getShortCode(){
						return getString("shortCode");
					}
			/** 
			* The size of the item in bytes
			*/ 
					public Long getSize(){
						return getLong("size");
					}
			/** 
			* The download URL
			*/ 
					public String getUrl(){
						return getString("url");
					}
	}
	
	
	GetLeaderboardEntriesResponse newGetLeaderboardEntriesResponse(Map data){
		return new GetLeaderboardEntriesResponse(data);
	}
	
	public class GetLeaderboardEntriesResponse extends AbstractResponse{
		public GetLeaderboardEntriesResponse(Map data){
			super(data);
		}
	
			/** 
			* The leaderboard entry data
			*/ 
					public GSData getResults(){
						return getObject("results");
					}
	}
	
	
	GetMessageResponse newGetMessageResponse(Map data){
		return new GetMessageResponse(data);
	}
	
	public class GetMessageResponse extends AbstractResponse{
		public GetMessageResponse(Map data){
			super(data);
		}
	
			/** 
			* The message data
			*/ 
					public GSData getMessage(){
						return getObject("message");
					}
			/** 
			* The message status
			*/ 
					public String getStatus(){
						return getString("status");
					}
	}
	
	
	GetMyTeamsResponse newGetMyTeamsResponse(Map data){
		return new GetMyTeamsResponse(data);
	}
	
	public class GetMyTeamsResponse extends AbstractResponse{
		public GetMyTeamsResponse(Map data){
			super(data);
		}
	
			/** 
			* The team data
			*/ 
					public List<Team> getTeams(){
						return getWrapperObjectList("teams", Team.class);
					}
	}
	
	
	GetPropertyResponse newGetPropertyResponse(Map data){
		return new GetPropertyResponse(data);
	}
	
	public class GetPropertyResponse extends AbstractResponse{
		public GetPropertyResponse(Map data){
			super(data);
		}
	
			/** 
			* The property value
			*/ 
					public GSData getProperty(){
						return getObject("property");
					}
	}
	
	
	GetPropertySetResponse newGetPropertySetResponse(Map data){
		return new GetPropertySetResponse(data);
	}
	
	public class GetPropertySetResponse extends AbstractResponse{
		public GetPropertySetResponse(Map data){
			super(data);
		}
	
			/** 
			* The property set
			*/ 
					public GSData getPropertySet(){
						return getObject("propertySet");
					}
	}
	
	
	GetTeamResponse newGetTeamResponse(Map data){
		return new GetTeamResponse(data);
	}
	
	public class GetTeamResponse extends AbstractResponse{
		public GetTeamResponse(Map data){
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
			/** 
			* A JSON array of teams.
			*/ 
					public List<Team> getTeams(){
						return getWrapperObjectList("teams", Team.class);
					}
	}
	
	
	GetUploadUrlResponse newGetUploadUrlResponse(Map data){
		return new GetUploadUrlResponse(data);
	}
	
	public class GetUploadUrlResponse extends AbstractResponse{
		public GetUploadUrlResponse(Map data){
			super(data);
		}
	
			/** 
			* The time sensitive upload URL
			*/ 
					public String getUrl(){
						return getString("url");
					}
	}
	
	
	GetUploadedResponse newGetUploadedResponse(Map data){
		return new GetUploadedResponse(data);
	}
	
	public class GetUploadedResponse extends AbstractResponse{
		public GetUploadedResponse(Map data){
			super(data);
		}
	
			/** 
			* The size of the file in bytes
			*/ 
					public Long getSize(){
						return getLong("size");
					}
			/** 
			* A time sensitive URL to a piece of content
			*/ 
					public String getUrl(){
						return getString("url");
					}
	}
	
	
	JoinChallengeResponse newJoinChallengeResponse(Map data){
		return new JoinChallengeResponse(data);
	}
	
	public class JoinChallengeResponse extends AbstractResponse{
		public JoinChallengeResponse(Map data){
			super(data);
		}
	
			/** 
			* Whether the player successfully joined the challenge
			*/ 
					public Boolean getJoined(){
						return getBoolean("joined");
					}
	}
	
	
	JoinPendingMatchResponse newJoinPendingMatchResponse(Map data){
		return new JoinPendingMatchResponse(data);
	}
	
	public class JoinPendingMatchResponse extends AbstractResponse{
		public JoinPendingMatchResponse(Map data){
			super(data);
		}
	
			/** 
			* A JSON object containing the new pending match
			*/ 
					public PendingMatch getPendingMatch(){
							if(getObject("pendingMatch") == null) { return null; }
							return new PendingMatch(getObject("pendingMatch"));
					}
	}
	
	
	JoinTeamResponse newJoinTeamResponse(Map data){
		return new JoinTeamResponse(data);
	}
	
	public class JoinTeamResponse extends AbstractResponse{
		public JoinTeamResponse(Map data){
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
	
	
	LeaderboardDataResponse newLeaderboardDataResponse(Map data){
		return new LeaderboardDataResponse(data);
	}
	
	public class LeaderboardDataResponse extends AbstractResponse{
		public LeaderboardDataResponse(Map data){
			super(data);
		}
	
			/** 
			* The leaderboard's challenge id
			*/ 
					public String getChallengeInstanceId(){
						return getString("challengeInstanceId");
					}
			/** 
			* The leaderboard data
			*/ 
					public List<LeaderboardData> getData(){
						return getWrapperObjectList("data", LeaderboardData.class);
					}
			/** 
			* The first item in the leaderboard data
			*/ 
					public List<LeaderboardData> getFirst(){
						return getWrapperObjectList("first", LeaderboardData.class);
					}
			/** 
			* The last item in the leaderboard data
			*/ 
					public List<LeaderboardData> getLast(){
						return getWrapperObjectList("last", LeaderboardData.class);
					}
			/** 
			* The leaderboard short code
			*/ 
					public String getLeaderboardShortCode(){
						return getString("leaderboardShortCode");
					}
	}
	
	
	LeaderboardsEntriesResponse newLeaderboardsEntriesResponse(Map data){
		return new LeaderboardsEntriesResponse(data);
	}
	
	public class LeaderboardsEntriesResponse extends AbstractResponse{
		public LeaderboardsEntriesResponse(Map data){
			super(data);
		}
	
			/** 
			* The leaderboard entry data
			*/ 
					public GSData getResults(){
						return getObject("results");
					}
	}
	
	
	LeaveTeamResponse newLeaveTeamResponse(Map data){
		return new LeaveTeamResponse(data);
	}
	
	public class LeaveTeamResponse extends AbstractResponse{
		public LeaveTeamResponse(Map data){
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
	
	
	ListAchievementsResponse newListAchievementsResponse(Map data){
		return new ListAchievementsResponse(data);
	}
	
	public class ListAchievementsResponse extends AbstractResponse{
		public ListAchievementsResponse(Map data){
			super(data);
		}
	
			/** 
			* A list of JSON achievment objects
			*/ 
					public List<Achievement> getAchievements(){
						return getWrapperObjectList("achievements", Achievement.class);
					}
	}
	
	
	ListBulkJobsAdminResponse newListBulkJobsAdminResponse(Map data){
		return new ListBulkJobsAdminResponse(data);
	}
	
	public class ListBulkJobsAdminResponse extends AbstractResponse{
		public ListBulkJobsAdminResponse(Map data){
			super(data);
		}
	
			/** 
			* A list of JSON objects containing bulk jobs
			*/ 
					public List<BulkJob> getBulkJobs(){
						return getWrapperObjectList("bulkJobs", BulkJob.class);
					}
	}
	
	
	ListChallengeResponse newListChallengeResponse(Map data){
		return new ListChallengeResponse(data);
	}
	
	public class ListChallengeResponse extends AbstractResponse{
		public ListChallengeResponse(Map data){
			super(data);
		}
	
			/** 
			* A list of JSON objects representing the challenges.
			*/ 
					public List<Challenge> getChallengeInstances(){
						return getWrapperObjectList("challengeInstances", Challenge.class);
					}
	}
	
	
	ListChallengeTypeResponse newListChallengeTypeResponse(Map data){
		return new ListChallengeTypeResponse(data);
	}
	
	public class ListChallengeTypeResponse extends AbstractResponse{
		public ListChallengeTypeResponse(Map data){
			super(data);
		}
	
			/** 
			* A list of JSON objects containing the challenge templates for the game
			*/ 
					public List<ChallengeType> getChallengeTemplates(){
						return getWrapperObjectList("challengeTemplates", ChallengeType.class);
					}
	}
	
	
	ListGameFriendsResponse newListGameFriendsResponse(Map data){
		return new ListGameFriendsResponse(data);
	}
	
	public class ListGameFriendsResponse extends AbstractResponse{
		public ListGameFriendsResponse(Map data){
			super(data);
		}
	
			/** 
			* A list of JSON objects containing game friend data
			*/ 
					public List<Player> getFriends(){
						return getWrapperObjectList("friends", Player.class);
					}
	}
	
	
	ListInviteFriendsResponse newListInviteFriendsResponse(Map data){
		return new ListInviteFriendsResponse(data);
	}
	
	public class ListInviteFriendsResponse extends AbstractResponse{
		public ListInviteFriendsResponse(Map data){
			super(data);
		}
	
			/** 
			* A list of JSON objects containing game friend data
			*/ 
					public List<InvitableFriend> getFriends(){
						return getWrapperObjectList("friends", InvitableFriend.class);
					}
	}
	
	
	ListLeaderboardsResponse newListLeaderboardsResponse(Map data){
		return new ListLeaderboardsResponse(data);
	}
	
	public class ListLeaderboardsResponse extends AbstractResponse{
		public ListLeaderboardsResponse(Map data){
			super(data);
		}
	
			/** 
			* A list of JSON object containing leaderboard meta data
			*/ 
					public List<Leaderboard> getLeaderboards(){
						return getWrapperObjectList("leaderboards", Leaderboard.class);
					}
	}
	
	
	ListMessageDetailResponse newListMessageDetailResponse(Map data){
		return new ListMessageDetailResponse(data);
	}
	
	public class ListMessageDetailResponse extends AbstractResponse{
		public ListMessageDetailResponse(Map data){
			super(data);
		}
	
			/** 
			* A list of JSON objects containing player messages
			*/ 
					public List<PlayerMessage> getMessageList(){
						return getWrapperObjectList("messageList", PlayerMessage.class);
					}
	}
	
	
	ListMessageResponse newListMessageResponse(Map data){
		return new ListMessageResponse(data);
	}
	
	public class ListMessageResponse extends AbstractResponse{
		public ListMessageResponse(Map data){
			super(data);
		}
	
			/** 
			* A list of JSON objects containing player messages
			*/ 
						public List<GSData> getMessageList(){
							return getObjectList("messageList");
						}
	}
	
	
	ListMessageSummaryResponse newListMessageSummaryResponse(Map data){
		return new ListMessageSummaryResponse(data);
	}
	
	public class ListMessageSummaryResponse extends AbstractResponse{
		public ListMessageSummaryResponse(Map data){
			super(data);
		}
	
			/** 
			* A list of JSON objects containing player message summaries
			*/ 
						public List<GSData> getMessageList(){
							return getObjectList("messageList");
						}
	}
	
	
	ListTeamChatResponse newListTeamChatResponse(Map data){
		return new ListTeamChatResponse(data);
	}
	
	public class ListTeamChatResponse extends AbstractResponse{
		public ListTeamChatResponse(Map data){
			super(data);
		}
	
			/** 
			* The collection of team chat messages
			*/ 
					public List<ChatMessage> getMessages(){
						return getWrapperObjectList("messages", ChatMessage.class);
					}
	}
	
	
	ListTeamsResponse newListTeamsResponse(Map data){
		return new ListTeamsResponse(data);
	}
	
	public class ListTeamsResponse extends AbstractResponse{
		public ListTeamsResponse(Map data){
			super(data);
		}
	
			/** 
			* A list of JSON objects containing team information
			*/ 
					public List<Team> getTeams(){
						return getWrapperObjectList("teams", Team.class);
					}
	}
	
	
	ListTransactionsResponse newListTransactionsResponse(Map data){
		return new ListTransactionsResponse(data);
	}
	
	public class ListTransactionsResponse extends AbstractResponse{
		public ListTransactionsResponse(Map data){
			super(data);
		}
	
			/** 
			* A list of JSON objects containing player transactions
			*/ 
					public List<PlayerTransaction> getTransactionList(){
						return getWrapperObjectList("transactionList", PlayerTransaction.class);
					}
	}
	
	
	ListVirtualGoodsResponse newListVirtualGoodsResponse(Map data){
		return new ListVirtualGoodsResponse(data);
	}
	
	public class ListVirtualGoodsResponse extends AbstractResponse{
		public ListVirtualGoodsResponse(Map data){
			super(data);
		}
	
			/** 
			* A list of JSON objects containing virtual goods data
			*/ 
					public List<VirtualGood> getVirtualGoods(){
						return getWrapperObjectList("virtualGoods", VirtualGood.class);
					}
	}
	
	
	LogChallengeEventResponse newLogChallengeEventResponse(Map data){
		return new LogChallengeEventResponse(data);
	}
	
	public class LogChallengeEventResponse extends AbstractResponse{
		public LogChallengeEventResponse(Map data){
			super(data);
		}
	
	}
	
	
	LogEventResponse newLogEventResponse(Map data){
		return new LogEventResponse(data);
	}
	
	public class LogEventResponse extends AbstractResponse{
		public LogEventResponse(Map data){
			super(data);
		}
	
	}
	
	
	MatchDetailsResponse newMatchDetailsResponse(Map data){
		return new MatchDetailsResponse(data);
	}
	
	public class MatchDetailsResponse extends AbstractResponse{
		public MatchDetailsResponse(Map data){
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
			* The id for this match instance
			*/ 
					public String getMatchId(){
						return getString("matchId");
					}
			/** 
			* The opponents this player has been matched against
			*/ 
					public List<Player> getOpponents(){
						return getWrapperObjectList("opponents", Player.class);
					}
			/** 
			* The peerId of this player within the match
			*/ 
					public Integer getPeerId(){
						return getInteger("peerId");
					}
			/** 
			* The id of the current player
			*/ 
					public String getPlayerId(){
						return getString("playerId");
					}
			/** 
			* The port to connect to for this match
			*/ 
					public Integer getPort(){
						return getInteger("port");
					}
	}
	
	
	MatchmakingResponse newMatchmakingResponse(Map data){
		return new MatchmakingResponse(data);
	}
	
	public class MatchmakingResponse extends AbstractResponse{
		public MatchmakingResponse(Map data){
			super(data);
		}
	
	}
	
	
	PushRegistrationResponse newPushRegistrationResponse(Map data){
		return new PushRegistrationResponse(data);
	}
	
	public class PushRegistrationResponse extends AbstractResponse{
		public PushRegistrationResponse(Map data){
			super(data);
		}
	
			/** 
			* An identifier for the successful registration.  Clients should store this value to be used in the event the player no longer wants to receive push notifications to this device.
			*/ 
					public String getRegistrationId(){
						return getString("registrationId");
					}
	}
	
	
	RegistrationResponse newRegistrationResponse(Map data){
		return new RegistrationResponse(data);
	}
	
	public class RegistrationResponse extends AbstractResponse{
		public RegistrationResponse(Map data){
			super(data);
		}
	
			/** 
			* 44b297a8-162a-4220-8c14-dad9a1946ad2
			*/ 
					public String getAuthToken(){
						return getString("authToken");
					}
			/** 
			* The player's display name
			*/ 
					public String getDisplayName(){
						return getString("displayName");
					}
			/** 
			* Indicates whether the player was created as part of this request
			*/ 
					public Boolean getNewPlayer(){
						return getBoolean("newPlayer");
					}
			/** 
			* A summary of the player that would be switched to.  Only returned as part of an error response for a request where automatic switching is disabled.
			*/ 
					public Player getSwitchSummary(){
							if(getObject("switchSummary") == null) { return null; }
							return new Player(getObject("switchSummary"));
					}
			/** 
			* The player's id
			*/ 
					public String getUserId(){
						return getString("userId");
					}
	}
	
	
	RevokePurchaseGoodsResponse newRevokePurchaseGoodsResponse(Map data){
		return new RevokePurchaseGoodsResponse(data);
	}
	
	public class RevokePurchaseGoodsResponse extends AbstractResponse{
		public RevokePurchaseGoodsResponse(Map data){
			super(data);
		}
	
			/** 
			* The map of revoked goods
			*/ 
					public GSData getRevokedGoods(){
						return getObject("revokedGoods");
					}
	}
	
	
	ScheduleBulkJobAdminResponse newScheduleBulkJobAdminResponse(Map data){
		return new ScheduleBulkJobAdminResponse(data);
	}
	
	public class ScheduleBulkJobAdminResponse extends AbstractResponse{
		public ScheduleBulkJobAdminResponse(Map data){
			super(data);
		}
	
			/** 
			* The count of players who would be affected by this job if it ran at the time it was submitted
			*/ 
					public Long getEstimatedCount(){
						return getLong("estimatedCount");
					}
			/** 
			* The unique job ID, used to identify this job in future requests
			*/ 
					public String getJobId(){
						return getString("jobId");
					}
	}
	
	
	SendFriendMessageResponse newSendFriendMessageResponse(Map data){
		return new SendFriendMessageResponse(data);
	}
	
	public class SendFriendMessageResponse extends AbstractResponse{
		public SendFriendMessageResponse(Map data){
			super(data);
		}
	
	}
	
	
	SendTeamChatMessageResponse newSendTeamChatMessageResponse(Map data){
		return new SendTeamChatMessageResponse(data);
	}
	
	public class SendTeamChatMessageResponse extends AbstractResponse{
		public SendTeamChatMessageResponse(Map data){
			super(data);
		}
	
	}
	
	
	SocialDisconnectResponse newSocialDisconnectResponse(Map data){
		return new SocialDisconnectResponse(data);
	}
	
	public class SocialDisconnectResponse extends AbstractResponse{
		public SocialDisconnectResponse(Map data){
			super(data);
		}
	
	}
	
	
	SocialStatusResponse newSocialStatusResponse(Map data){
		return new SocialStatusResponse(data);
	}
	
	public class SocialStatusResponse extends AbstractResponse{
		public SocialStatusResponse(Map data){
			super(data);
		}
	
			/** 
			* A list of social statuses.
			*/ 
					public List<SocialStatus> getStatuses(){
						return getWrapperObjectList("statuses", SocialStatus.class);
					}
	}
	
	
	UpdateMessageResponse newUpdateMessageResponse(Map data){
		return new UpdateMessageResponse(data);
	}
	
	public class UpdateMessageResponse extends AbstractResponse{
		public UpdateMessageResponse(Map data){
			super(data);
		}
	
	}
	
	
	WithdrawChallengeResponse newWithdrawChallengeResponse(Map data){
		return new WithdrawChallengeResponse(data);
	}
	
	public class WithdrawChallengeResponse extends AbstractResponse{
		public WithdrawChallengeResponse(Map data){
			super(data);
		}
	
			/** 
			* A challenge instance id
			*/ 
					public String getChallengeInstanceId(){
						return getString("challengeInstanceId");
					}
	}
	
	
}

