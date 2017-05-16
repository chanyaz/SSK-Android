package com.gamesparks.sdk.api.autogen;

import java.util.Date;
import java.util.Map;
import java.util.List;

import com.gamesparks.sdk.GS;
import com.gamesparks.sdk.api.GSData;
import com.gamesparks.sdk.api.AbstractRequest;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder.*;

public class GSRequestBuilder {
	
	GS gs;
	
	GSResponseBuilder responseBuilder = new GSResponseBuilder();
	
	public GSRequestBuilder(GS gs){
		this.gs = gs;
	}
		
	
	public AcceptChallengeRequest createAcceptChallengeRequest(){
		return new AcceptChallengeRequest(gs);
	}
	
	public class AcceptChallengeRequest extends AbstractRequest<AcceptChallengeResponse>
	{
		private AcceptChallengeRequest(GS gs) {
			super(gs);
			data.put("@class", ".AcceptChallengeRequest");
		}
		
		protected AcceptChallengeResponse createResponse(Map data) {
			return responseBuilder.newAcceptChallengeResponse(data);
		}
		
		protected AcceptChallengeRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The ID of the challenge
		*/
		public AcceptChallengeRequest setChallengeInstanceId( String challengeInstanceId )
		{
			data.put("challengeInstanceId", challengeInstanceId);
			return this;
		}
		
		
		/**
		* An optional message to send with the challenge
		*/
		public AcceptChallengeRequest setMessage( String message )
		{
			data.put("message", message);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public AcceptChallengeRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public AccountDetailsRequest createAccountDetailsRequest(){
		return new AccountDetailsRequest(gs);
	}
	
	public class AccountDetailsRequest extends AbstractRequest<AccountDetailsResponse>
	{
		private AccountDetailsRequest(GS gs) {
			super(gs);
			data.put("@class", ".AccountDetailsRequest");
		}
		
		protected AccountDetailsResponse createResponse(Map data) {
			return responseBuilder.newAccountDetailsResponse(data);
		}
		
		protected AccountDetailsRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public AccountDetailsRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public AmazonBuyGoodsRequest createAmazonBuyGoodsRequest(){
		return new AmazonBuyGoodsRequest(gs);
	}
	
	public class AmazonBuyGoodsRequest extends AbstractRequest<BuyVirtualGoodResponse>
	{
		private AmazonBuyGoodsRequest(GS gs) {
			super(gs);
			data.put("@class", ".AmazonBuyGoodsRequest");
		}
		
		protected BuyVirtualGoodResponse createResponse(Map data) {
			return responseBuilder.newBuyVirtualGoodResponse(data);
		}
		
		protected AmazonBuyGoodsRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The userId obtained from the UserData within a PurchaseResponse
		*/
		public AmazonBuyGoodsRequest setAmazonUserId( String amazonUserId )
		{
			data.put("amazonUserId", amazonUserId);
			return this;
		}
		
		
		/**
		* The receiptId obtained from the Receipt within a PurchaseResponse
		*/
		public AmazonBuyGoodsRequest setReceiptId( String receiptId )
		{
			data.put("receiptId", receiptId);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public AmazonBuyGoodsRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* If set to true, the transactionId from this receipt will not be globally valdidated, this will mean replays between players are possible.
		* It will only validate the transactionId has not been used by this player before.
		*/
		public AmazonBuyGoodsRequest setUniqueTransactionByPlayer( boolean uniqueTransactionByPlayer )
		{
			data.put("uniqueTransactionByPlayer", uniqueTransactionByPlayer);
			return this;
		}
		
		
		
				
	}
	
	
	public AmazonConnectRequest createAmazonConnectRequest(){
		return new AmazonConnectRequest(gs);
	}
	
	public class AmazonConnectRequest extends AbstractRequest<AuthenticationResponse>
	{
		private AmazonConnectRequest(GS gs) {
			super(gs);
			data.put("@class", ".AmazonConnectRequest");
		}
		
		protected AuthenticationResponse createResponse(Map data) {
			return responseBuilder.newAuthenticationResponse(data);
		}
		
		protected AmazonConnectRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The access token is used by the client to make authenticated requests on behalf of the end user.
		*/
		public AmazonConnectRequest setAccessToken( String accessToken )
		{
			data.put("accessToken", accessToken);
			return this;
		}
		
		
		/**
		* Indicates that the server should not try to link the external profile with the current player.  If false, links the external profile to the currently signed in player.  If true, creates a new player and links the external profile to them.  Defaults to false.
		*/
		public AmazonConnectRequest setDoNotLinkToCurrentPlayer( boolean doNotLinkToCurrentPlayer )
		{
			data.put("doNotLinkToCurrentPlayer", doNotLinkToCurrentPlayer);
			return this;
		}
		
		
		/**
		* Indicates whether the server should return an error if an account switch would have occurred, rather than switching automatically.  Defaults to false.
		*/
		public AmazonConnectRequest setErrorOnSwitch( boolean errorOnSwitch )
		{
			data.put("errorOnSwitch", errorOnSwitch);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public AmazonConnectRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* An optional segment configuration for this request.
		* If this request creates a new player on the gamesparks platform, the segments of the new player will match the values provided
		*/
		public AmazonConnectRequest setSegments( Map<String,Object> segments )
		{
			data.put("segments", segments);
			return this;
		}
		
		
		/**
		* Indicates that the server should switch to the supplied profile if it isalready associated to a player. Defaults to false.
		*/
		public AmazonConnectRequest setSwitchIfPossible( boolean switchIfPossible )
		{
			data.put("switchIfPossible", switchIfPossible);
			return this;
		}
		
		
		/**
		* Indicates that the associated players displayName should be kept in syn with this profile when it's updated by the external provider.
		*/
		public AmazonConnectRequest setSyncDisplayName( boolean syncDisplayName )
		{
			data.put("syncDisplayName", syncDisplayName);
			return this;
		}
		
		
		
				
	}
	
	
	public AnalyticsRequest createAnalyticsRequest(){
		return new AnalyticsRequest(gs);
	}
	
	public class AnalyticsRequest extends AbstractRequest<AnalyticsResponse>
	{
		private AnalyticsRequest(GS gs) {
			super(gs);
			data.put("@class", ".AnalyticsRequest");
		}
		
		protected AnalyticsResponse createResponse(Map data) {
			return responseBuilder.newAnalyticsResponse(data);
		}
		
		protected AnalyticsRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* Custom data payload
		*/
		public AnalyticsRequest setData( Map<String,Object> data )
		{
			data.put("data", data);
			return this;
		}
		
		
		/**
		* Use the value true to indicate it's an end timer
		*/
		public AnalyticsRequest setEnd( boolean end )
		{
			data.put("end", end);
			return this;
		}
		
		
		/**
		* The key you want to track this analysis with.
		*/
		public AnalyticsRequest setKey( String key )
		{
			data.put("key", key);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public AnalyticsRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* Use the value true to indicate it's a start timer
		*/
		public AnalyticsRequest setStart( boolean start )
		{
			data.put("start", start);
			return this;
		}
		
		
		
				
	}
	
	
	public AroundMeLeaderboardRequest createAroundMeLeaderboardRequest(){
		return new AroundMeLeaderboardRequest(gs);
	}
	
	public class AroundMeLeaderboardRequest extends AbstractRequest<AroundMeLeaderboardResponse>
	{
		private AroundMeLeaderboardRequest(GS gs) {
			super(gs);
			data.put("@class", ".AroundMeLeaderboardRequest");
		}
		
		protected AroundMeLeaderboardResponse createResponse(Map data) {
			return responseBuilder.newAroundMeLeaderboardResponse(data);
		}
		
		protected AroundMeLeaderboardRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The challenge instance to get the leaderboard data for
		*/
		public AroundMeLeaderboardRequest setChallengeInstanceId( String challengeInstanceId )
		{
			data.put("challengeInstanceId", challengeInstanceId);
			return this;
		}
		
		
		/**
		* An optional filter on the customIds.
		* If this request does not contain a custonIdFilter or if it is a partial filter, 
		* the leaderboard entries around the highest score for the given constraints will be returned.
		*/
		public AroundMeLeaderboardRequest setCustomIdFilter( Map<String,Object> customIdFilter )
		{
			data.put("customIdFilter", customIdFilter);
			return this;
		}
		
		
		/**
		* The default behaviour on a social request is to error if the player has no friends (NOTSOCIAL).  Set this flag to suppress that error and return the player's leaderboard entry instead.
		*/
		public AroundMeLeaderboardRequest setDontErrorOnNotSocial( boolean dontErrorOnNotSocial )
		{
			data.put("dontErrorOnNotSocial", dontErrorOnNotSocial);
			return this;
		}
		
		
		/**
		* The number of items to return in a page (default=50)
		*/
		public AroundMeLeaderboardRequest setEntryCount( long entryCount )
		{
			data.put("entryCount", entryCount);
			return this;
		}
		
		
		/**
		* A friend id or an array of friend ids to use instead of the player's social friends
		*/
		public AroundMeLeaderboardRequest setFriendIds( List<String> friendIds )
		{
			data.put("friendIds", friendIds);
			return this;
		}
		
		
		/**
		* Number of entries to include from head of the list
		*/
		public AroundMeLeaderboardRequest setIncludeFirst( long includeFirst )
		{
			data.put("includeFirst", includeFirst);
			return this;
		}
		
		
		/**
		* Number of entries to include from tail of the list
		*/
		public AroundMeLeaderboardRequest setIncludeLast( long includeLast )
		{
			data.put("includeLast", includeLast);
			return this;
		}
		
		
		/**
		* Returns the leaderboard excluding the player's social friends
		*/
		public AroundMeLeaderboardRequest setInverseSocial( boolean inverseSocial )
		{
			data.put("inverseSocial", inverseSocial);
			return this;
		}
		
		
		/**
		* The short code of the leaderboard
		*/
		public AroundMeLeaderboardRequest setLeaderboardShortCode( String leaderboardShortCode )
		{
			data.put("leaderboardShortCode", leaderboardShortCode);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public AroundMeLeaderboardRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* If True returns a leaderboard of the player's social friends
		*/
		public AroundMeLeaderboardRequest setSocial( boolean social )
		{
			data.put("social", social);
			return this;
		}
		
		
		/**
		* The IDs of the teams you are interested in
		*/
		public AroundMeLeaderboardRequest setTeamIds( List<String> teamIds )
		{
			data.put("teamIds", teamIds);
			return this;
		}
		
		
		/**
		* The type of team you are interested in
		*/
		public AroundMeLeaderboardRequest setTeamTypes( List<String> teamTypes )
		{
			data.put("teamTypes", teamTypes);
			return this;
		}
		
		
		
				
	}
	
	
	public AuthenticationRequest createAuthenticationRequest(){
		return new AuthenticationRequest(gs);
	}
	
	public class AuthenticationRequest extends AbstractRequest<AuthenticationResponse>
	{
		private AuthenticationRequest(GS gs) {
			super(gs);
			data.put("@class", ".AuthenticationRequest");
		}
		
		protected AuthenticationResponse createResponse(Map data) {
			return responseBuilder.newAuthenticationResponse(data);
		}
		
		protected AuthenticationRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The previously registered password
		*/
		public AuthenticationRequest setPassword( String password )
		{
			data.put("password", password);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public AuthenticationRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The previously registered player name
		*/
		public AuthenticationRequest setUserName( String userName )
		{
			data.put("userName", userName);
			return this;
		}
		
		
		
				
	}
	
	
	public BatchAdminRequest createBatchAdminRequest(){
		return new BatchAdminRequest(gs);
	}
	
	public class BatchAdminRequest extends AbstractRequest<BatchAdminResponse>
	{
		private BatchAdminRequest(GS gs) {
			super(gs);
			data.put("@class", ".BatchAdminRequest");
		}
		
		protected BatchAdminResponse createResponse(Map data) {
			return responseBuilder.newBatchAdminResponse(data);
		}
		
		protected BatchAdminRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The players to run the request for.
		*/
		public BatchAdminRequest setPlayerIds( List<String> playerIds )
		{
			data.put("playerIds", playerIds);
			return this;
		}
		
		
		/**
		* The request to be run for each player.
		*/
		public BatchAdminRequest setRequest( Map<String,Object> request )
		{
			data.put("request", request);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public BatchAdminRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public BuyVirtualGoodsRequest createBuyVirtualGoodsRequest(){
		return new BuyVirtualGoodsRequest(gs);
	}
	
	public class BuyVirtualGoodsRequest extends AbstractRequest<BuyVirtualGoodResponse>
	{
		private BuyVirtualGoodsRequest(GS gs) {
			super(gs);
			data.put("@class", ".BuyVirtualGoodsRequest");
		}
		
		protected BuyVirtualGoodResponse createResponse(Map data) {
			return responseBuilder.newBuyVirtualGoodResponse(data);
		}
		
		protected BuyVirtualGoodsRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* Which virtual currency to use. (1 to 6)
		*/
		public BuyVirtualGoodsRequest setCurrencyType( long currencyType )
		{
			data.put("currencyType", currencyType);
			return this;
		}
		
		
		/**
		* The number of items to purchase
		*/
		public BuyVirtualGoodsRequest setQuantity( long quantity )
		{
			data.put("quantity", quantity);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public BuyVirtualGoodsRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The short code of the virtual good to be purchased
		*/
		public BuyVirtualGoodsRequest setShortCode( String shortCode )
		{
			data.put("shortCode", shortCode);
			return this;
		}
		
		
		
				
	}
	
	
	public CancelBulkJobAdminRequest createCancelBulkJobAdminRequest(){
		return new CancelBulkJobAdminRequest(gs);
	}
	
	public class CancelBulkJobAdminRequest extends AbstractRequest<CancelBulkJobAdminResponse>
	{
		private CancelBulkJobAdminRequest(GS gs) {
			super(gs);
			data.put("@class", ".CancelBulkJobAdminRequest");
		}
		
		protected CancelBulkJobAdminResponse createResponse(Map data) {
			return responseBuilder.newCancelBulkJobAdminResponse(data);
		}
		
		protected CancelBulkJobAdminRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The IDs of existing bulk jobs to cancel
		*/
		public CancelBulkJobAdminRequest setBulkJobIds( List<String> bulkJobIds )
		{
			data.put("bulkJobIds", bulkJobIds);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public CancelBulkJobAdminRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public ChangeUserDetailsRequest createChangeUserDetailsRequest(){
		return new ChangeUserDetailsRequest(gs);
	}
	
	public class ChangeUserDetailsRequest extends AbstractRequest<ChangeUserDetailsResponse>
	{
		private ChangeUserDetailsRequest(GS gs) {
			super(gs);
			data.put("@class", ".ChangeUserDetailsRequest");
		}
		
		protected ChangeUserDetailsResponse createResponse(Map data) {
			return responseBuilder.newChangeUserDetailsResponse(data);
		}
		
		protected ChangeUserDetailsRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The new display name to set in the player data.
		*/
		public ChangeUserDetailsRequest setDisplayName( String displayName )
		{
			data.put("displayName", displayName);
			return this;
		}
		
		
		/**
		* The new language code to set in the player data.
		*/
		public ChangeUserDetailsRequest setLanguage( String language )
		{
			data.put("language", language);
			return this;
		}
		
		
		/**
		* The new password to set in the player data.
		*/
		public ChangeUserDetailsRequest setNewPassword( String newPassword )
		{
			data.put("newPassword", newPassword);
			return this;
		}
		
		
		/**
		* The player's existing password. If supplied it will be checked against the password stored in the player data. This allows you re-authenticate the player making the change.
		*/
		public ChangeUserDetailsRequest setOldPassword( String oldPassword )
		{
			data.put("oldPassword", oldPassword);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public ChangeUserDetailsRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The new userName with which this player will sign in.  If the player currently authenticates using device authentication this will upgrade their account and require them to use username and password authentication from now on.
		*/
		public ChangeUserDetailsRequest setUserName( String userName )
		{
			data.put("userName", userName);
			return this;
		}
		
		
		
				
	}
	
	
	public ChatOnChallengeRequest createChatOnChallengeRequest(){
		return new ChatOnChallengeRequest(gs);
	}
	
	public class ChatOnChallengeRequest extends AbstractRequest<ChatOnChallengeResponse>
	{
		private ChatOnChallengeRequest(GS gs) {
			super(gs);
			data.put("@class", ".ChatOnChallengeRequest");
		}
		
		protected ChatOnChallengeResponse createResponse(Map data) {
			return responseBuilder.newChatOnChallengeResponse(data);
		}
		
		protected ChatOnChallengeRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The ID of the challenge
		*/
		public ChatOnChallengeRequest setChallengeInstanceId( String challengeInstanceId )
		{
			data.put("challengeInstanceId", challengeInstanceId);
			return this;
		}
		
		
		/**
		* An optional message to send with the challenge
		*/
		public ChatOnChallengeRequest setMessage( String message )
		{
			data.put("message", message);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public ChatOnChallengeRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public ConsumeVirtualGoodRequest createConsumeVirtualGoodRequest(){
		return new ConsumeVirtualGoodRequest(gs);
	}
	
	public class ConsumeVirtualGoodRequest extends AbstractRequest<ConsumeVirtualGoodResponse>
	{
		private ConsumeVirtualGoodRequest(GS gs) {
			super(gs);
			data.put("@class", ".ConsumeVirtualGoodRequest");
		}
		
		protected ConsumeVirtualGoodResponse createResponse(Map data) {
			return responseBuilder.newConsumeVirtualGoodResponse(data);
		}
		
		protected ConsumeVirtualGoodRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The amount of virtual goods to be consumed
		*/
		public ConsumeVirtualGoodRequest setQuantity( long quantity )
		{
			data.put("quantity", quantity);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public ConsumeVirtualGoodRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The short code of the virtual good to be consumed
		*/
		public ConsumeVirtualGoodRequest setShortCode( String shortCode )
		{
			data.put("shortCode", shortCode);
			return this;
		}
		
		
		
				
	}
	
	
	public CreateChallengeRequest createCreateChallengeRequest(){
		return new CreateChallengeRequest(gs);
	}
	
	public class CreateChallengeRequest extends AbstractRequest<CreateChallengeResponse>
	{
		private CreateChallengeRequest(GS gs) {
			super(gs);
			data.put("@class", ".CreateChallengeRequest");
		}
		
		protected CreateChallengeResponse createResponse(Map data) {
			return responseBuilder.newCreateChallengeResponse(data);
		}
		
		protected CreateChallengeRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* Who can join this challenge. Either PUBLIC, PRIVATE or FRIENDS
		*/
		public CreateChallengeRequest setAccessType( String accessType )
		{
			data.put("accessType", accessType);
			return this;
		}
		
		
		/**
		* Whether this challenge should automatically start when a new player joins and maxPlayers is reached
		*/
		public CreateChallengeRequest setAutoStartJoinedChallengeOnMaxPlayers( boolean autoStartJoinedChallengeOnMaxPlayers )
		{
			data.put("autoStartJoinedChallengeOnMaxPlayers", autoStartJoinedChallengeOnMaxPlayers);
			return this;
		}
		
		
		/**
		* An optional message to include with the challenge
		*/
		public CreateChallengeRequest setChallengeMessage( String challengeMessage )
		{
			data.put("challengeMessage", challengeMessage);
			return this;
		}
		
		
		/**
		* The short code of the challenge
		*/
		public CreateChallengeRequest setChallengeShortCode( String challengeShortCode )
		{
			data.put("challengeShortCode", challengeShortCode);
			return this;
		}
		
		
		/**
		* The ammount of currency type 1 that the player is wagering on this challenge
		*/
		public CreateChallengeRequest setCurrency1Wager( long currency1Wager )
		{
			data.put("currency1Wager", currency1Wager);
			return this;
		}
		
		
		/**
		* The amount of currency type 2 that the player is wagering on this challenge
		*/
		public CreateChallengeRequest setCurrency2Wager( long currency2Wager )
		{
			data.put("currency2Wager", currency2Wager);
			return this;
		}
		
		
		/**
		* The amount of currency type 3 that the player is wagering on this challenge
		*/
		public CreateChallengeRequest setCurrency3Wager( long currency3Wager )
		{
			data.put("currency3Wager", currency3Wager);
			return this;
		}
		
		
		/**
		* The amount of currency type 4 that the player is wagering on this challenge
		*/
		public CreateChallengeRequest setCurrency4Wager( long currency4Wager )
		{
			data.put("currency4Wager", currency4Wager);
			return this;
		}
		
		
		/**
		* The amount of currency type 5 that the player is wagering on this challenge
		*/
		public CreateChallengeRequest setCurrency5Wager( long currency5Wager )
		{
			data.put("currency5Wager", currency5Wager);
			return this;
		}
		
		
		/**
		* The amount of currency type 6 that the player is wagering on this challenge
		*/
		public CreateChallengeRequest setCurrency6Wager( long currency6Wager )
		{
			data.put("currency6Wager", currency6Wager);
			return this;
		}
		
		
		/**
		* Criteria for who can and cannot find and join this challenge (when the accessType is PUBLIC or FRIENDS).
		* Currently supports a field <i>segments</i> that contains segment type against single (where that segment value is required) or multiple (where one of the specified values is required) segment values.
		* For each segment type a player must have one of the specified values in order to be considered eligible.
		*/
		public CreateChallengeRequest setEligibilityCriteria( Map<String,Object> eligibilityCriteria )
		{
			data.put("eligibilityCriteria", eligibilityCriteria);
			return this;
		}
		
		
		/**
		* The time at which this challenge will end
		*/
		public CreateChallengeRequest setEndTime( Date endTime )
		{
			setDateField("endTime", endTime);
			return this;
		}
		
		
		/**
		* The latest time that players can join this challenge
		*/
		public CreateChallengeRequest setExpiryTime( Date expiryTime )
		{
			setDateField("expiryTime", expiryTime);
			return this;
		}
		
		
		/**
		* The maximum number of attempts 
		*/
		public CreateChallengeRequest setMaxAttempts( long maxAttempts )
		{
			data.put("maxAttempts", maxAttempts);
			return this;
		}
		
		
		/**
		* The maximum number of players that are allowed to join this challenge
		*/
		public CreateChallengeRequest setMaxPlayers( long maxPlayers )
		{
			data.put("maxPlayers", maxPlayers);
			return this;
		}
		
		
		/**
		* The minimum number of players that are allowed to join this challenge
		*/
		public CreateChallengeRequest setMinPlayers( long minPlayers )
		{
			data.put("minPlayers", minPlayers);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public CreateChallengeRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* If True  no messaging is triggered
		*/
		public CreateChallengeRequest setSilent( boolean silent )
		{
			data.put("silent", silent);
			return this;
		}
		
		
		/**
		* The time at which this challenge will begin
		*/
		public CreateChallengeRequest setStartTime( Date startTime )
		{
			setDateField("startTime", startTime);
			return this;
		}
		
		
		/**
		* A player id or an array of player ids who will recieve this challenge
		*/
		public CreateChallengeRequest setUsersToChallenge( List<String> usersToChallenge )
		{
			data.put("usersToChallenge", usersToChallenge);
			return this;
		}
		
		
		
				
	}
	
	
	public CreateTeamRequest createCreateTeamRequest(){
		return new CreateTeamRequest(gs);
	}
	
	public class CreateTeamRequest extends AbstractRequest<CreateTeamResponse>
	{
		private CreateTeamRequest(GS gs) {
			super(gs);
			data.put("@class", ".CreateTeamRequest");
		}
		
		protected CreateTeamResponse createResponse(Map data) {
			return responseBuilder.newCreateTeamResponse(data);
		}
		
		protected CreateTeamRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public CreateTeamRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* An optional teamId to use
		*/
		public CreateTeamRequest setTeamId( String teamId )
		{
			data.put("teamId", teamId);
			return this;
		}
		
		
		/**
		* A display name to use
		*/
		public CreateTeamRequest setTeamName( String teamName )
		{
			data.put("teamName", teamName);
			return this;
		}
		
		
		/**
		* The type of team to be created
		*/
		public CreateTeamRequest setTeamType( String teamType )
		{
			data.put("teamType", teamType);
			return this;
		}
		
		
		
				
	}
	
	
	public DeclineChallengeRequest createDeclineChallengeRequest(){
		return new DeclineChallengeRequest(gs);
	}
	
	public class DeclineChallengeRequest extends AbstractRequest<DeclineChallengeResponse>
	{
		private DeclineChallengeRequest(GS gs) {
			super(gs);
			data.put("@class", ".DeclineChallengeRequest");
		}
		
		protected DeclineChallengeResponse createResponse(Map data) {
			return responseBuilder.newDeclineChallengeResponse(data);
		}
		
		protected DeclineChallengeRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The ID of the challenge
		*/
		public DeclineChallengeRequest setChallengeInstanceId( String challengeInstanceId )
		{
			data.put("challengeInstanceId", challengeInstanceId);
			return this;
		}
		
		
		/**
		* An optional message to send with the challenge
		*/
		public DeclineChallengeRequest setMessage( String message )
		{
			data.put("message", message);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public DeclineChallengeRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public DeviceAuthenticationRequest createDeviceAuthenticationRequest(){
		return new DeviceAuthenticationRequest(gs);
	}
	
	public class DeviceAuthenticationRequest extends AbstractRequest<AuthenticationResponse>
	{
		private DeviceAuthenticationRequest(GS gs) {
			super(gs);
			data.put("@class", ".DeviceAuthenticationRequest");
		}
		
		protected AuthenticationResponse createResponse(Map data) {
			return responseBuilder.newAuthenticationResponse(data);
		}
		
		protected DeviceAuthenticationRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* A unique device identifier. Each platform has it's own method for getting a unique id
		*/
		public DeviceAuthenticationRequest setDeviceId( String deviceId )
		{
			data.put("deviceId", deviceId);
			return this;
		}
		
		
		/**
		* The device model
		*/
		public DeviceAuthenticationRequest setDeviceModel( String deviceModel )
		{
			data.put("deviceModel", deviceModel);
			return this;
		}
		
		
		/**
		* The device name
		*/
		public DeviceAuthenticationRequest setDeviceName( String deviceName )
		{
			data.put("deviceName", deviceName);
			return this;
		}
		
		
		/**
		* An indicator of the device platform, should be IOS, ANDROID, WP8 or W8
		*/
		public DeviceAuthenticationRequest setDeviceOS( String deviceOS )
		{
			data.put("deviceOS", deviceOS);
			return this;
		}
		
		
		/**
		* The device type
		*/
		public DeviceAuthenticationRequest setDeviceType( String deviceType )
		{
			data.put("deviceType", deviceType);
			return this;
		}
		
		
		/**
		* An optional displayname for the player
		*/
		public DeviceAuthenticationRequest setDisplayName( String displayName )
		{
			data.put("displayName", displayName);
			return this;
		}
		
		
		/**
		* The device type
		*/
		public DeviceAuthenticationRequest setOperatingSystem( String operatingSystem )
		{
			data.put("operatingSystem", operatingSystem);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public DeviceAuthenticationRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* An optional segment configuration for this request.
		* If this request reates a new player on the gamesparks platform, the segments of the new player will match the values provided
		*/
		public DeviceAuthenticationRequest setSegments( Map<String,Object> segments )
		{
			data.put("segments", segments);
			return this;
		}
		
		
		
				
	}
	
	
	public DismissMessageRequest createDismissMessageRequest(){
		return new DismissMessageRequest(gs);
	}
	
	public class DismissMessageRequest extends AbstractRequest<DismissMessageResponse>
	{
		private DismissMessageRequest(GS gs) {
			super(gs);
			data.put("@class", ".DismissMessageRequest");
		}
		
		protected DismissMessageResponse createResponse(Map data) {
			return responseBuilder.newDismissMessageResponse(data);
		}
		
		protected DismissMessageRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The messageId of the message to dismiss
		*/
		public DismissMessageRequest setMessageId( String messageId )
		{
			data.put("messageId", messageId);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public DismissMessageRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public DismissMultipleMessagesRequest createDismissMultipleMessagesRequest(){
		return new DismissMultipleMessagesRequest(gs);
	}
	
	public class DismissMultipleMessagesRequest extends AbstractRequest<DismissMultipleMessagesResponse>
	{
		private DismissMultipleMessagesRequest(GS gs) {
			super(gs);
			data.put("@class", ".DismissMultipleMessagesRequest");
		}
		
		protected DismissMultipleMessagesResponse createResponse(Map data) {
			return responseBuilder.newDismissMultipleMessagesResponse(data);
		}
		
		protected DismissMultipleMessagesRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The list of the messageIds to dismiss
		*/
		public DismissMultipleMessagesRequest setMessageIds( List<String> messageIds )
		{
			data.put("messageIds", messageIds);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public DismissMultipleMessagesRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public DropTeamRequest createDropTeamRequest(){
		return new DropTeamRequest(gs);
	}
	
	public class DropTeamRequest extends AbstractRequest<DropTeamResponse>
	{
		private DropTeamRequest(GS gs) {
			super(gs);
			data.put("@class", ".DropTeamRequest");
		}
		
		protected DropTeamResponse createResponse(Map data) {
			return responseBuilder.newDropTeamResponse(data);
		}
		
		protected DropTeamRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The team owner to find, used in combination with teamType. If not supplied the current players id will be used
		*/
		public DropTeamRequest setOwnerId( String ownerId )
		{
			data.put("ownerId", ownerId);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public DropTeamRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The teamId to find (may be null if teamType supplied)
		*/
		public DropTeamRequest setTeamId( String teamId )
		{
			data.put("teamId", teamId);
			return this;
		}
		
		
		/**
		* The teamType to find, used in combination with the current player, or the player defined by ownerId
		*/
		public DropTeamRequest setTeamType( String teamType )
		{
			data.put("teamType", teamType);
			return this;
		}
		
		
		
				
	}
	
	
	public EndSessionRequest createEndSessionRequest(){
		return new EndSessionRequest(gs);
	}
	
	public class EndSessionRequest extends AbstractRequest<EndSessionResponse>
	{
		private EndSessionRequest(GS gs) {
			super(gs);
			data.put("@class", ".EndSessionRequest");
		}
		
		protected EndSessionResponse createResponse(Map data) {
			return responseBuilder.newEndSessionResponse(data);
		}
		
		protected EndSessionRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public EndSessionRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public FacebookConnectRequest createFacebookConnectRequest(){
		return new FacebookConnectRequest(gs);
	}
	
	public class FacebookConnectRequest extends AbstractRequest<AuthenticationResponse>
	{
		private FacebookConnectRequest(GS gs) {
			super(gs);
			data.put("@class", ".FacebookConnectRequest");
		}
		
		protected AuthenticationResponse createResponse(Map data) {
			return responseBuilder.newAuthenticationResponse(data);
		}
		
		protected FacebookConnectRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The access token is used by the client to make authenticated requests on behalf of the end user.
		* It has a longer lifetime than the authorization code, typically on the order of minutes or hours.
		* When the access token expires, attempts to use it will fail, and a new access token must be obtained via a refresh token.
		*/
		public FacebookConnectRequest setAccessToken( String accessToken )
		{
			data.put("accessToken", accessToken);
			return this;
		}
		
		
		/**
		* An authorization code is a short-lived token representing the user's access grant, created by the authorization server and passed to the client application via the browser.
		* The client application sends the authorization code to the authorization server to obtain an access token and, optionally, a refresh token.
		*/
		public FacebookConnectRequest setCode( String code )
		{
			data.put("code", code);
			return this;
		}
		
		
		/**
		* Indicates that the server should not try to link the external profile with the current player.  If false, links the external profile to the currently signed in player.  If true, creates a new player and links the external profile to them.  Defaults to false.
		*/
		public FacebookConnectRequest setDoNotLinkToCurrentPlayer( boolean doNotLinkToCurrentPlayer )
		{
			data.put("doNotLinkToCurrentPlayer", doNotLinkToCurrentPlayer);
			return this;
		}
		
		
		/**
		* Indicates whether the server should return an error if an account switch would have occurred, rather than switching automatically.  Defaults to false.
		*/
		public FacebookConnectRequest setErrorOnSwitch( boolean errorOnSwitch )
		{
			data.put("errorOnSwitch", errorOnSwitch);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public FacebookConnectRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* An optional segment configuration for this request.
		* If this request creates a new player on the gamesparks platform, the segments of the new player will match the values provided
		*/
		public FacebookConnectRequest setSegments( Map<String,Object> segments )
		{
			data.put("segments", segments);
			return this;
		}
		
		
		/**
		* Indicates that the server should switch to the supplied profile if it isalready associated to a player. Defaults to false.
		*/
		public FacebookConnectRequest setSwitchIfPossible( boolean switchIfPossible )
		{
			data.put("switchIfPossible", switchIfPossible);
			return this;
		}
		
		
		/**
		* Indicates that the associated players displayName should be kept in syn with this profile when it's updated by the external provider.
		*/
		public FacebookConnectRequest setSyncDisplayName( boolean syncDisplayName )
		{
			data.put("syncDisplayName", syncDisplayName);
			return this;
		}
		
		
		
				
	}
	
	
	public FindChallengeRequest createFindChallengeRequest(){
		return new FindChallengeRequest(gs);
	}
	
	public class FindChallengeRequest extends AbstractRequest<FindChallengeResponse>
	{
		private FindChallengeRequest(GS gs) {
			super(gs);
			data.put("@class", ".FindChallengeRequest");
		}
		
		protected FindChallengeResponse createResponse(Map data) {
			return responseBuilder.newFindChallengeResponse(data);
		}
		
		protected FindChallengeRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The type of challenge to find, either PUBLIC or FRIENDS.  Defaults to FRIENDS
		*/
		public FindChallengeRequest setAccessType( String accessType )
		{
			data.put("accessType", accessType);
			return this;
		}
		
		
		/**
		* The number of challenges to return (MAX=50)
		*/
		public FindChallengeRequest setCount( long count )
		{
			data.put("count", count);
			return this;
		}
		
		
		/**
		* Optional.  Allows the current player's eligibility to be overridden by what is provided here.
		*/
		public FindChallengeRequest setEligibility( Map<String,Object> eligibility )
		{
			data.put("eligibility", eligibility);
			return this;
		}
		
		
		/**
		* The offset to start from when returning challenges (used for paging)
		*/
		public FindChallengeRequest setOffset( long offset )
		{
			data.put("offset", offset);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public FindChallengeRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* Optional shortCodes to filter the results by challenge type
		*/
		public FindChallengeRequest setShortCode( List<String> shortCode )
		{
			data.put("shortCode", shortCode);
			return this;
		}
		
		
		
				
	}
	
	
	public FindMatchRequest createFindMatchRequest(){
		return new FindMatchRequest(gs);
	}
	
	public class FindMatchRequest extends AbstractRequest<FindMatchResponse>
	{
		private FindMatchRequest(GS gs) {
			super(gs);
			data.put("@class", ".FindMatchRequest");
		}
		
		protected FindMatchResponse createResponse(Map data) {
			return responseBuilder.newFindMatchResponse(data);
		}
		
		protected FindMatchRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The action to take on the already in-flight request for this match. Currently supported actions are: 'cancel'
		*/
		public FindMatchRequest setAction( String action )
		{
			data.put("action", action);
			return this;
		}
		
		
		/**
		* Optional. Players will be grouped based on the distinct value passed in here, only players in the same group can be matched together
		*/
		public FindMatchRequest setMatchGroup( String matchGroup )
		{
			data.put("matchGroup", matchGroup);
			return this;
		}
		
		
		/**
		* The shortCode of the match type this player is registering for
		*/
		public FindMatchRequest setMatchShortCode( String matchShortCode )
		{
			data.put("matchShortCode", matchShortCode);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public FindMatchRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The skill of the player looking for a match
		*/
		public FindMatchRequest setSkill( long skill )
		{
			data.put("skill", skill);
			return this;
		}
		
		
		
				
	}
	
	
	public FindPendingMatchesRequest createFindPendingMatchesRequest(){
		return new FindPendingMatchesRequest(gs);
	}
	
	public class FindPendingMatchesRequest extends AbstractRequest<FindPendingMatchesResponse>
	{
		private FindPendingMatchesRequest(GS gs) {
			super(gs);
			data.put("@class", ".FindPendingMatchesRequest");
		}
		
		protected FindPendingMatchesResponse createResponse(Map data) {
			return responseBuilder.newFindPendingMatchesResponse(data);
		}
		
		protected FindPendingMatchesRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* Optional. The matchGroup of the match this player previously registeredfor
		*/
		public FindPendingMatchesRequest setMatchGroup( String matchGroup )
		{
			data.put("matchGroup", matchGroup);
			return this;
		}
		
		
		/**
		* The shortCode of the match this player previously registered for
		*/
		public FindPendingMatchesRequest setMatchShortCode( String matchShortCode )
		{
			data.put("matchShortCode", matchShortCode);
			return this;
		}
		
		
		/**
		* Optional. The maximum number of pending matches to return (default=10)
		*/
		public FindPendingMatchesRequest setMaxMatchesToFind( long maxMatchesToFind )
		{
			data.put("maxMatchesToFind", maxMatchesToFind);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public FindPendingMatchesRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public GameCenterConnectRequest createGameCenterConnectRequest(){
		return new GameCenterConnectRequest(gs);
	}
	
	public class GameCenterConnectRequest extends AbstractRequest<AuthenticationResponse>
	{
		private GameCenterConnectRequest(GS gs) {
			super(gs);
			data.put("@class", ".GameCenterConnectRequest");
		}
		
		protected AuthenticationResponse createResponse(Map data) {
			return responseBuilder.newAuthenticationResponse(data);
		}
		
		protected GameCenterConnectRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The display of the current player from GameCenter. This will be used as the displayName of the gamesparks player if created (or syncDisplayname is true)
		*/
		public GameCenterConnectRequest setDisplayName( String displayName )
		{
			data.put("displayName", displayName);
			return this;
		}
		
		
		/**
		* Indicates that the server should not try to link the external profile with the current player.  If false, links the external profile to the currently signed in player.  If true, creates a new player and links the external profile to them.  Defaults to false.
		*/
		public GameCenterConnectRequest setDoNotLinkToCurrentPlayer( boolean doNotLinkToCurrentPlayer )
		{
			data.put("doNotLinkToCurrentPlayer", doNotLinkToCurrentPlayer);
			return this;
		}
		
		
		/**
		* Indicates whether the server should return an error if an account switch would have occurred, rather than switching automatically.  Defaults to false.
		*/
		public GameCenterConnectRequest setErrorOnSwitch( boolean errorOnSwitch )
		{
			data.put("errorOnSwitch", errorOnSwitch);
			return this;
		}
		
		
		/**
		* The game center id of the current player. This value obtained be obtained from GKLocalPlayer playerID
		*/
		public GameCenterConnectRequest setExternalPlayerId( String externalPlayerId )
		{
			data.put("externalPlayerId", externalPlayerId);
			return this;
		}
		
		
		/**
		* The url from where we will download the public key. This value should be obtained from generateIdentityVerificationSignatureWithCompletionHandler. 
		*/
		public GameCenterConnectRequest setPublicKeyUrl( String publicKeyUrl )
		{
			data.put("publicKeyUrl", publicKeyUrl);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public GameCenterConnectRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The salt is needed in order to prevent request forgery. This value should be obtained from generateIdentityVerificationSignatureWithCompletionHandler and should be base64 encoded using [salt base64Encoding]
		*/
		public GameCenterConnectRequest setSalt( String salt )
		{
			data.put("salt", salt);
			return this;
		}
		
		
		/**
		* An optional segment configuration for this request.
		* If this request creates a new player on the gamesparks platform, the segments of the new player will match the values provided
		*/
		public GameCenterConnectRequest setSegments( Map<String,Object> segments )
		{
			data.put("segments", segments);
			return this;
		}
		
		
		/**
		* The signature is needed to validate that the request is genuine and that we can save the player. This value should be obtained from generateIdentityVerificationSignatureWithCompletionHandler and should be base64 encoded using [signature base64Encoding]
		*/
		public GameCenterConnectRequest setSignature( String signature )
		{
			data.put("signature", signature);
			return this;
		}
		
		
		/**
		* Indicates that the server should switch to the supplied profile if it isalready associated to a player. Defaults to false.
		*/
		public GameCenterConnectRequest setSwitchIfPossible( boolean switchIfPossible )
		{
			data.put("switchIfPossible", switchIfPossible);
			return this;
		}
		
		
		/**
		* Indicates that the associated players displayName should be kept in syn with this profile when it's updated by the external provider.
		*/
		public GameCenterConnectRequest setSyncDisplayName( boolean syncDisplayName )
		{
			data.put("syncDisplayName", syncDisplayName);
			return this;
		}
		
		
		/**
		* The timestamp is needed to validate the request signature. This value should be obtained from generateIdentityVerificationSignatureWithCompletionHandler
		*/
		public GameCenterConnectRequest setTimestamp( long timestamp )
		{
			data.put("timestamp", timestamp);
			return this;
		}
		
		
		
				
	}
	
	
	public GetChallengeRequest createGetChallengeRequest(){
		return new GetChallengeRequest(gs);
	}
	
	public class GetChallengeRequest extends AbstractRequest<GetChallengeResponse>
	{
		private GetChallengeRequest(GS gs) {
			super(gs);
			data.put("@class", ".GetChallengeRequest");
		}
		
		protected GetChallengeResponse createResponse(Map data) {
			return responseBuilder.newGetChallengeResponse(data);
		}
		
		protected GetChallengeRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The ID of the challenge
		*/
		public GetChallengeRequest setChallengeInstanceId( String challengeInstanceId )
		{
			data.put("challengeInstanceId", challengeInstanceId);
			return this;
		}
		
		
		/**
		* An optional message to send with the challenge
		*/
		public GetChallengeRequest setMessage( String message )
		{
			data.put("message", message);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public GetChallengeRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public GetDownloadableRequest createGetDownloadableRequest(){
		return new GetDownloadableRequest(gs);
	}
	
	public class GetDownloadableRequest extends AbstractRequest<GetDownloadableResponse>
	{
		private GetDownloadableRequest(GS gs) {
			super(gs);
			data.put("@class", ".GetDownloadableRequest");
		}
		
		protected GetDownloadableResponse createResponse(Map data) {
			return responseBuilder.newGetDownloadableResponse(data);
		}
		
		protected GetDownloadableRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public GetDownloadableRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The short code of the Downloadable item
		*/
		public GetDownloadableRequest setShortCode( String shortCode )
		{
			data.put("shortCode", shortCode);
			return this;
		}
		
		
		
				
	}
	
	
	public GetLeaderboardEntriesRequest createGetLeaderboardEntriesRequest(){
		return new GetLeaderboardEntriesRequest(gs);
	}
	
	public class GetLeaderboardEntriesRequest extends AbstractRequest<GetLeaderboardEntriesResponse>
	{
		private GetLeaderboardEntriesRequest(GS gs) {
			super(gs);
			data.put("@class", ".GetLeaderboardEntriesRequest");
		}
		
		protected GetLeaderboardEntriesResponse createResponse(Map data) {
			return responseBuilder.newGetLeaderboardEntriesResponse(data);
		}
		
		protected GetLeaderboardEntriesRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The challenge leaderboards to return entries for
		*/
		public GetLeaderboardEntriesRequest setChallenges( List<String> challenges )
		{
			data.put("challenges", challenges);
			return this;
		}
		
		
		/**
		* Returns the leaderboard excluding the player's social friends
		*/
		public GetLeaderboardEntriesRequest setInverseSocial( boolean inverseSocial )
		{
			data.put("inverseSocial", inverseSocial);
			return this;
		}
		
		
		/**
		* The list of leaderboards shortcodes
		*/
		public GetLeaderboardEntriesRequest setLeaderboards( List<String> leaderboards )
		{
			data.put("leaderboards", leaderboards);
			return this;
		}
		
		
		/**
		* The player id. Leave out to use the current player id
		*/
		public GetLeaderboardEntriesRequest setPlayer( String player )
		{
			data.put("player", player);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public GetLeaderboardEntriesRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* Set to true to include the player's game friends
		*/
		public GetLeaderboardEntriesRequest setSocial( boolean social )
		{
			data.put("social", social);
			return this;
		}
		
		
		/**
		* The types of team to apply this request to
		*/
		public GetLeaderboardEntriesRequest setTeamTypes( List<String> teamTypes )
		{
			data.put("teamTypes", teamTypes);
			return this;
		}
		
		
		
				
	}
	
	
	public GetMessageRequest createGetMessageRequest(){
		return new GetMessageRequest(gs);
	}
	
	public class GetMessageRequest extends AbstractRequest<GetMessageResponse>
	{
		private GetMessageRequest(GS gs) {
			super(gs);
			data.put("@class", ".GetMessageRequest");
		}
		
		protected GetMessageResponse createResponse(Map data) {
			return responseBuilder.newGetMessageResponse(data);
		}
		
		protected GetMessageRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The messageId of the message retreive
		*/
		public GetMessageRequest setMessageId( String messageId )
		{
			data.put("messageId", messageId);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public GetMessageRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public GetMyTeamsRequest createGetMyTeamsRequest(){
		return new GetMyTeamsRequest(gs);
	}
	
	public class GetMyTeamsRequest extends AbstractRequest<GetMyTeamsResponse>
	{
		private GetMyTeamsRequest(GS gs) {
			super(gs);
			data.put("@class", ".GetMyTeamsRequest");
		}
		
		protected GetMyTeamsResponse createResponse(Map data) {
			return responseBuilder.newGetMyTeamsResponse(data);
		}
		
		protected GetMyTeamsRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* Set to true to only get teams owned by the player
		*/
		public GetMyTeamsRequest setOwnedOnly( boolean ownedOnly )
		{
			data.put("ownedOnly", ownedOnly);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public GetMyTeamsRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The type of teams to get
		*/
		public GetMyTeamsRequest setTeamTypes( List<String> teamTypes )
		{
			data.put("teamTypes", teamTypes);
			return this;
		}
		
		
		
				
	}
	
	
	public GetPropertyRequest createGetPropertyRequest(){
		return new GetPropertyRequest(gs);
	}
	
	public class GetPropertyRequest extends AbstractRequest<GetPropertyResponse>
	{
		private GetPropertyRequest(GS gs) {
			super(gs);
			data.put("@class", ".GetPropertyRequest");
		}
		
		protected GetPropertyResponse createResponse(Map data) {
			return responseBuilder.newGetPropertyResponse(data);
		}
		
		protected GetPropertyRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The shortCode of the property to return.
		*/
		public GetPropertyRequest setPropertyShortCode( String propertyShortCode )
		{
			data.put("propertyShortCode", propertyShortCode);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public GetPropertyRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public GetPropertySetRequest createGetPropertySetRequest(){
		return new GetPropertySetRequest(gs);
	}
	
	public class GetPropertySetRequest extends AbstractRequest<GetPropertySetResponse>
	{
		private GetPropertySetRequest(GS gs) {
			super(gs);
			data.put("@class", ".GetPropertySetRequest");
		}
		
		protected GetPropertySetResponse createResponse(Map data) {
			return responseBuilder.newGetPropertySetResponse(data);
		}
		
		protected GetPropertySetRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The shortCode of the property set to return.
		*/
		public GetPropertySetRequest setPropertySetShortCode( String propertySetShortCode )
		{
			data.put("propertySetShortCode", propertySetShortCode);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public GetPropertySetRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public GetTeamRequest createGetTeamRequest(){
		return new GetTeamRequest(gs);
	}
	
	public class GetTeamRequest extends AbstractRequest<GetTeamResponse>
	{
		private GetTeamRequest(GS gs) {
			super(gs);
			data.put("@class", ".GetTeamRequest");
		}
		
		protected GetTeamResponse createResponse(Map data) {
			return responseBuilder.newGetTeamResponse(data);
		}
		
		protected GetTeamRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The team owner to find, used in combination with teamType. If not supplied the current players id will be used
		*/
		public GetTeamRequest setOwnerId( String ownerId )
		{
			data.put("ownerId", ownerId);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public GetTeamRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The teamId to find (may be null if teamType supplied)
		*/
		public GetTeamRequest setTeamId( String teamId )
		{
			data.put("teamId", teamId);
			return this;
		}
		
		
		/**
		* The teamType to find, used in combination with the current player, or the player defined by ownerId
		*/
		public GetTeamRequest setTeamType( String teamType )
		{
			data.put("teamType", teamType);
			return this;
		}
		
		
		
				
	}
	
	
	public GetUploadUrlRequest createGetUploadUrlRequest(){
		return new GetUploadUrlRequest(gs);
	}
	
	public class GetUploadUrlRequest extends AbstractRequest<GetUploadUrlResponse>
	{
		private GetUploadUrlRequest(GS gs) {
			super(gs);
			data.put("@class", ".GetUploadUrlRequest");
		}
		
		protected GetUploadUrlResponse createResponse(Map data) {
			return responseBuilder.newGetUploadUrlResponse(data);
		}
		
		protected GetUploadUrlRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public GetUploadUrlRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* Optional meta data which is stored against the player's uploaded content
		*/
		public GetUploadUrlRequest setUploadData( Map<String,Object> uploadData )
		{
			data.put("uploadData", uploadData);
			return this;
		}
		
		
		
				
	}
	
	
	public GetUploadedRequest createGetUploadedRequest(){
		return new GetUploadedRequest(gs);
	}
	
	public class GetUploadedRequest extends AbstractRequest<GetUploadedResponse>
	{
		private GetUploadedRequest(GS gs) {
			super(gs);
			data.put("@class", ".GetUploadedRequest");
		}
		
		protected GetUploadedResponse createResponse(Map data) {
			return responseBuilder.newGetUploadedResponse(data);
		}
		
		protected GetUploadedRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public GetUploadedRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The system generated id of the uploaded item
		*/
		public GetUploadedRequest setUploadId( String uploadId )
		{
			data.put("uploadId", uploadId);
			return this;
		}
		
		
		
				
	}
	
	
	public GooglePlayBuyGoodsRequest createGooglePlayBuyGoodsRequest(){
		return new GooglePlayBuyGoodsRequest(gs);
	}
	
	public class GooglePlayBuyGoodsRequest extends AbstractRequest<BuyVirtualGoodResponse>
	{
		private GooglePlayBuyGoodsRequest(GS gs) {
			super(gs);
			data.put("@class", ".GooglePlayBuyGoodsRequest");
		}
		
		protected BuyVirtualGoodResponse createResponse(Map data) {
			return responseBuilder.newBuyVirtualGoodResponse(data);
		}
		
		protected GooglePlayBuyGoodsRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public GooglePlayBuyGoodsRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The value obtained from data.getStringExtra("INAPP_DATA_SIGNATURE");
		*/
		public GooglePlayBuyGoodsRequest setSignature( String signature )
		{
			data.put("signature", signature);
			return this;
		}
		
		
		/**
		* The value obtained from data.getStringExtra("INAPP_PURCHASE_DATA")
		*/
		public GooglePlayBuyGoodsRequest setSignedData( String signedData )
		{
			data.put("signedData", signedData);
			return this;
		}
		
		
		/**
		* If set to true, the transactionId from this receipt will not be globally valdidated, this will mean replays between players are possible.
		* It will only validate the transactionId has not been used by this player before.
		*/
		public GooglePlayBuyGoodsRequest setUniqueTransactionByPlayer( boolean uniqueTransactionByPlayer )
		{
			data.put("uniqueTransactionByPlayer", uniqueTransactionByPlayer);
			return this;
		}
		
		
		
				
	}
	
	
	public GooglePlayConnectRequest createGooglePlayConnectRequest(){
		return new GooglePlayConnectRequest(gs);
	}
	
	public class GooglePlayConnectRequest extends AbstractRequest<AuthenticationResponse>
	{
		private GooglePlayConnectRequest(GS gs) {
			super(gs);
			data.put("@class", ".GooglePlayConnectRequest");
		}
		
		protected AuthenticationResponse createResponse(Map data) {
			return responseBuilder.newAuthenticationResponse(data);
		}
		
		protected GooglePlayConnectRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The access token is used when using the service id and certificate.
		*/
		public GooglePlayConnectRequest setAccessToken( String accessToken )
		{
			data.put("accessToken", accessToken);
			return this;
		}
		
		
		/**
		* The access code is used by the client to make authenticated requests on behalf of the end user. Requires clientId and clientsecret to be set
		*/
		public GooglePlayConnectRequest setCode( String code )
		{
			data.put("code", code);
			return this;
		}
		
		
		/**
		* The display of the current player from Google Play. This will be used as the displayName of the gamesparks player if created (or syncDisplayname is true)
		*/
		public GooglePlayConnectRequest setDisplayName( String displayName )
		{
			data.put("displayName", displayName);
			return this;
		}
		
		
		/**
		* Indicates that the server should not try to link the external profile with the current player.  If false, links the external profile to the currently signed in player.  If true, creates a new player and links the external profile to them.  Defaults to false.
		*/
		public GooglePlayConnectRequest setDoNotLinkToCurrentPlayer( boolean doNotLinkToCurrentPlayer )
		{
			data.put("doNotLinkToCurrentPlayer", doNotLinkToCurrentPlayer);
			return this;
		}
		
		
		/**
		* Indicates whether the server should return an error if an account switch would have occurred, rather than switching automatically.  Defaults to false.
		*/
		public GooglePlayConnectRequest setErrorOnSwitch( boolean errorOnSwitch )
		{
			data.put("errorOnSwitch", errorOnSwitch);
			return this;
		}
		
		
		/**
		* Did you request the plus.login scope when you got the access code or authorisation token from Google? If this is true, we will fetch the user's google+ account and friends
		*/
		public GooglePlayConnectRequest setGooglePlusScope( boolean googlePlusScope )
		{
			data.put("googlePlusScope", googlePlusScope);
			return this;
		}
		
		
		/**
		* Did you request the profile scope when you got the access code or authorisation token from Google? If this is true, we will fetch the user info by calling https://www.googleapis.com/oauth2/v1/userinfo?alt=json 
		*/
		public GooglePlayConnectRequest setProfileScope( boolean profileScope )
		{
			data.put("profileScope", profileScope);
			return this;
		}
		
		
		/**
		* Only required when the access code has been granted using an explicit redirectUri, for example when using the mechanism described in https://developers.google.com/+/web/signin/server-side-flow
		*/
		public GooglePlayConnectRequest setRedirectUri( String redirectUri )
		{
			data.put("redirectUri", redirectUri);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public GooglePlayConnectRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* An optional segment configuration for this request.
		* If this request creates a new player on the gamesparks platform, the segments of the new player will match the values provided
		*/
		public GooglePlayConnectRequest setSegments( Map<String,Object> segments )
		{
			data.put("segments", segments);
			return this;
		}
		
		
		/**
		* Indicates that the server should switch to the supplied profile if it isalready associated to a player. Defaults to false.
		*/
		public GooglePlayConnectRequest setSwitchIfPossible( boolean switchIfPossible )
		{
			data.put("switchIfPossible", switchIfPossible);
			return this;
		}
		
		
		/**
		* Indicates that the associated players displayName should be kept in syn with this profile when it's updated by the external provider.
		*/
		public GooglePlayConnectRequest setSyncDisplayName( boolean syncDisplayName )
		{
			data.put("syncDisplayName", syncDisplayName);
			return this;
		}
		
		
		
				
	}
	
	
	public GooglePlusConnectRequest createGooglePlusConnectRequest(){
		return new GooglePlusConnectRequest(gs);
	}
	
	public class GooglePlusConnectRequest extends AbstractRequest<AuthenticationResponse>
	{
		private GooglePlusConnectRequest(GS gs) {
			super(gs);
			data.put("@class", ".GooglePlusConnectRequest");
		}
		
		protected AuthenticationResponse createResponse(Map data) {
			return responseBuilder.newAuthenticationResponse(data);
		}
		
		protected GooglePlusConnectRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The access token is used when using the service id and certificate.
		*/
		public GooglePlusConnectRequest setAccessToken( String accessToken )
		{
			data.put("accessToken", accessToken);
			return this;
		}
		
		
		/**
		* The access code is used by the client to make authenticated requests on behalf of the end user. Requires clientId and clientsecret to be set
		*/
		public GooglePlusConnectRequest setCode( String code )
		{
			data.put("code", code);
			return this;
		}
		
		
		/**
		* Indicates that the server should not try to link the external profile with the current player.  If false, links the external profile to the currently signed in player.  If true, creates a new player and links the external profile to them.  Defaults to false.
		*/
		public GooglePlusConnectRequest setDoNotLinkToCurrentPlayer( boolean doNotLinkToCurrentPlayer )
		{
			data.put("doNotLinkToCurrentPlayer", doNotLinkToCurrentPlayer);
			return this;
		}
		
		
		/**
		* Indicates whether the server should return an error if an account switch would have occurred, rather than switching automatically.  Defaults to false.
		*/
		public GooglePlusConnectRequest setErrorOnSwitch( boolean errorOnSwitch )
		{
			data.put("errorOnSwitch", errorOnSwitch);
			return this;
		}
		
		
		/**
		* Only required when the access code has been granted using an explicit redirectUri, for example when using the mechanism described in https://developers.google.com/+/web/signin/server-side-flow
		*/
		public GooglePlusConnectRequest setRedirectUri( String redirectUri )
		{
			data.put("redirectUri", redirectUri);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public GooglePlusConnectRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* An optional segment configuration for this request.
		* If this request creates a new player on the gamesparks platform, the segments of the new player will match the values provided
		*/
		public GooglePlusConnectRequest setSegments( Map<String,Object> segments )
		{
			data.put("segments", segments);
			return this;
		}
		
		
		/**
		* Indicates that the server should switch to the supplied profile if it isalready associated to a player. Defaults to false.
		*/
		public GooglePlusConnectRequest setSwitchIfPossible( boolean switchIfPossible )
		{
			data.put("switchIfPossible", switchIfPossible);
			return this;
		}
		
		
		/**
		* Indicates that the associated players displayName should be kept in syn with this profile when it's updated by the external provider.
		*/
		public GooglePlusConnectRequest setSyncDisplayName( boolean syncDisplayName )
		{
			data.put("syncDisplayName", syncDisplayName);
			return this;
		}
		
		
		
				
	}
	
	
	public IOSBuyGoodsRequest createIOSBuyGoodsRequest(){
		return new IOSBuyGoodsRequest(gs);
	}
	
	public class IOSBuyGoodsRequest extends AbstractRequest<BuyVirtualGoodResponse>
	{
		private IOSBuyGoodsRequest(GS gs) {
			super(gs);
			data.put("@class", ".IOSBuyGoodsRequest");
		}
		
		protected BuyVirtualGoodResponse createResponse(Map data) {
			return responseBuilder.newBuyVirtualGoodResponse(data);
		}
		
		protected IOSBuyGoodsRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The receipt obtained from SKPaymentTransaction. transactionReceipt
		*/
		public IOSBuyGoodsRequest setReceipt( String receipt )
		{
			data.put("receipt", receipt);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public IOSBuyGoodsRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* Should the sandbox account be used
		*/
		public IOSBuyGoodsRequest setSandbox( boolean sandbox )
		{
			data.put("sandbox", sandbox);
			return this;
		}
		
		
		/**
		* If set to true, the transactionId from this receipt will not be globally valdidated, this will mean replays between players are possible.
		* It will only validate the transactionId has not been used by this player before.
		*/
		public IOSBuyGoodsRequest setUniqueTransactionByPlayer( boolean uniqueTransactionByPlayer )
		{
			data.put("uniqueTransactionByPlayer", uniqueTransactionByPlayer);
			return this;
		}
		
		
		
				
	}
	
	
	public JoinChallengeRequest createJoinChallengeRequest(){
		return new JoinChallengeRequest(gs);
	}
	
	public class JoinChallengeRequest extends AbstractRequest<JoinChallengeResponse>
	{
		private JoinChallengeRequest(GS gs) {
			super(gs);
			data.put("@class", ".JoinChallengeRequest");
		}
		
		protected JoinChallengeResponse createResponse(Map data) {
			return responseBuilder.newJoinChallengeResponse(data);
		}
		
		protected JoinChallengeRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The ID of the challenge
		*/
		public JoinChallengeRequest setChallengeInstanceId( String challengeInstanceId )
		{
			data.put("challengeInstanceId", challengeInstanceId);
			return this;
		}
		
		
		/**
		* Optional.  Allows the current player's eligibility to be overridden by what is provided here.
		*/
		public JoinChallengeRequest setEligibility( Map<String,Object> eligibility )
		{
			data.put("eligibility", eligibility);
			return this;
		}
		
		
		/**
		* An optional message to send with the challenge
		*/
		public JoinChallengeRequest setMessage( String message )
		{
			data.put("message", message);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public JoinChallengeRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public JoinPendingMatchRequest createJoinPendingMatchRequest(){
		return new JoinPendingMatchRequest(gs);
	}
	
	public class JoinPendingMatchRequest extends AbstractRequest<JoinPendingMatchResponse>
	{
		private JoinPendingMatchRequest(GS gs) {
			super(gs);
			data.put("@class", ".JoinPendingMatchRequest");
		}
		
		protected JoinPendingMatchResponse createResponse(Map data) {
			return responseBuilder.newJoinPendingMatchResponse(data);
		}
		
		protected JoinPendingMatchRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* Optional. The matchGroup of the match this player previously registeredfor
		*/
		public JoinPendingMatchRequest setMatchGroup( String matchGroup )
		{
			data.put("matchGroup", matchGroup);
			return this;
		}
		
		
		/**
		* The shortCode of the match this player previously registered for
		*/
		public JoinPendingMatchRequest setMatchShortCode( String matchShortCode )
		{
			data.put("matchShortCode", matchShortCode);
			return this;
		}
		
		
		/**
		* The pending match ID to join
		*/
		public JoinPendingMatchRequest setPendingMatchId( String pendingMatchId )
		{
			data.put("pendingMatchId", pendingMatchId);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public JoinPendingMatchRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public JoinTeamRequest createJoinTeamRequest(){
		return new JoinTeamRequest(gs);
	}
	
	public class JoinTeamRequest extends AbstractRequest<JoinTeamResponse>
	{
		private JoinTeamRequest(GS gs) {
			super(gs);
			data.put("@class", ".JoinTeamRequest");
		}
		
		protected JoinTeamResponse createResponse(Map data) {
			return responseBuilder.newJoinTeamResponse(data);
		}
		
		protected JoinTeamRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The team owner to find, used in combination with teamType. If not supplied the current players id will be used
		*/
		public JoinTeamRequest setOwnerId( String ownerId )
		{
			data.put("ownerId", ownerId);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public JoinTeamRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The teamId to find (may be null if teamType supplied)
		*/
		public JoinTeamRequest setTeamId( String teamId )
		{
			data.put("teamId", teamId);
			return this;
		}
		
		
		/**
		* The teamType to find, used in combination with the current player, or the player defined by ownerId
		*/
		public JoinTeamRequest setTeamType( String teamType )
		{
			data.put("teamType", teamType);
			return this;
		}
		
		
		
				
	}
	
	
	public KongregateConnectRequest createKongregateConnectRequest(){
		return new KongregateConnectRequest(gs);
	}
	
	public class KongregateConnectRequest extends AbstractRequest<AuthenticationResponse>
	{
		private KongregateConnectRequest(GS gs) {
			super(gs);
			data.put("@class", ".KongregateConnectRequest");
		}
		
		protected AuthenticationResponse createResponse(Map data) {
			return responseBuilder.newAuthenticationResponse(data);
		}
		
		protected KongregateConnectRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* Indicates that the server should not try to link the external profile with the current player.  If false, links the external profile to the currently signed in player.  If true, creates a new player and links the external profile to them.  Defaults to false.
		*/
		public KongregateConnectRequest setDoNotLinkToCurrentPlayer( boolean doNotLinkToCurrentPlayer )
		{
			data.put("doNotLinkToCurrentPlayer", doNotLinkToCurrentPlayer);
			return this;
		}
		
		
		/**
		* Indicates whether the server should return an error if an account switch would have occurred, rather than switching automatically.  Defaults to false.
		*/
		public KongregateConnectRequest setErrorOnSwitch( boolean errorOnSwitch )
		{
			data.put("errorOnSwitch", errorOnSwitch);
			return this;
		}
		
		
		/**
		* The gameAuthToken, together with the userID are used by the client to make authenticated requests on behalf of the end user.
		*/
		public KongregateConnectRequest setGameAuthToken( String gameAuthToken )
		{
			data.put("gameAuthToken", gameAuthToken);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public KongregateConnectRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* An optional segment configuration for this request.
		* If this request creates a new player on the gamesparks platform, the segments of the new player will match the values provided
		*/
		public KongregateConnectRequest setSegments( Map<String,Object> segments )
		{
			data.put("segments", segments);
			return this;
		}
		
		
		/**
		* Indicates that the server should switch to the supplied profile if it isalready associated to a player. Defaults to false.
		*/
		public KongregateConnectRequest setSwitchIfPossible( boolean switchIfPossible )
		{
			data.put("switchIfPossible", switchIfPossible);
			return this;
		}
		
		
		/**
		* Indicates that the associated players displayName should be kept in syn with this profile when it's updated by the external provider.
		*/
		public KongregateConnectRequest setSyncDisplayName( boolean syncDisplayName )
		{
			data.put("syncDisplayName", syncDisplayName);
			return this;
		}
		
		
		/**
		* The userID, together with the gameAuthToken are used by the client to make authenticated requests on behalf of the end user.
		*/
		public KongregateConnectRequest setUserId( String userId )
		{
			data.put("userId", userId);
			return this;
		}
		
		
		
				
	}
	
	
	public LeaderboardDataRequest createLeaderboardDataRequest(){
		return new LeaderboardDataRequest(gs);
	}
	
	public class LeaderboardDataRequest extends AbstractRequest<LeaderboardDataResponse>
	{
		private LeaderboardDataRequest(GS gs) {
			super(gs);
			data.put("@class", ".LeaderboardDataRequest");
		}
		
		protected LeaderboardDataResponse createResponse(Map data) {
			return responseBuilder.newLeaderboardDataResponse(data);
		}
		
		protected LeaderboardDataRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The challenge instance to get the leaderboard data for
		*/
		public LeaderboardDataRequest setChallengeInstanceId( String challengeInstanceId )
		{
			data.put("challengeInstanceId", challengeInstanceId);
			return this;
		}
		
		
		/**
		* The default behaviour on a social request is to error if the player has no friends (NOTSOCIAL).  Set this flag to suppress that error and return the player's leaderboard entry instead.
		*/
		public LeaderboardDataRequest setDontErrorOnNotSocial( boolean dontErrorOnNotSocial )
		{
			data.put("dontErrorOnNotSocial", dontErrorOnNotSocial);
			return this;
		}
		
		
		/**
		* The number of items to return in a page (default=50)
		*/
		public LeaderboardDataRequest setEntryCount( long entryCount )
		{
			data.put("entryCount", entryCount);
			return this;
		}
		
		
		/**
		* A friend id or an array of friend ids to use instead of the player's social friends
		*/
		public LeaderboardDataRequest setFriendIds( List<String> friendIds )
		{
			data.put("friendIds", friendIds);
			return this;
		}
		
		
		/**
		* Number of entries to include from head of the list
		*/
		public LeaderboardDataRequest setIncludeFirst( long includeFirst )
		{
			data.put("includeFirst", includeFirst);
			return this;
		}
		
		
		/**
		* Number of entries to include from tail of the list
		*/
		public LeaderboardDataRequest setIncludeLast( long includeLast )
		{
			data.put("includeLast", includeLast);
			return this;
		}
		
		
		/**
		* Returns the leaderboard excluding the player's social friends
		*/
		public LeaderboardDataRequest setInverseSocial( boolean inverseSocial )
		{
			data.put("inverseSocial", inverseSocial);
			return this;
		}
		
		
		/**
		* The short code of the leaderboard
		*/
		public LeaderboardDataRequest setLeaderboardShortCode( String leaderboardShortCode )
		{
			data.put("leaderboardShortCode", leaderboardShortCode);
			return this;
		}
		
		
		/**
		* The offset into the set of leaderboards returned
		*/
		public LeaderboardDataRequest setOffset( long offset )
		{
			data.put("offset", offset);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public LeaderboardDataRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* If True returns a leaderboard of the player's social friends
		*/
		public LeaderboardDataRequest setSocial( boolean social )
		{
			data.put("social", social);
			return this;
		}
		
		
		/**
		* The IDs of the teams you are interested in
		*/
		public LeaderboardDataRequest setTeamIds( List<String> teamIds )
		{
			data.put("teamIds", teamIds);
			return this;
		}
		
		
		/**
		* The type of team you are interested in
		*/
		public LeaderboardDataRequest setTeamTypes( List<String> teamTypes )
		{
			data.put("teamTypes", teamTypes);
			return this;
		}
		
		
		
				
	}
	
	
	public LeaderboardsEntriesRequest createLeaderboardsEntriesRequest(){
		return new LeaderboardsEntriesRequest(gs);
	}
	
	public class LeaderboardsEntriesRequest extends AbstractRequest<LeaderboardsEntriesResponse>
	{
		private LeaderboardsEntriesRequest(GS gs) {
			super(gs);
			data.put("@class", ".LeaderboardsEntriesRequest");
		}
		
		protected LeaderboardsEntriesResponse createResponse(Map data) {
			return responseBuilder.newLeaderboardsEntriesResponse(data);
		}
		
		protected LeaderboardsEntriesRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The challenge leaderboards to return entries for
		*/
		public LeaderboardsEntriesRequest setChallenges( List<String> challenges )
		{
			data.put("challenges", challenges);
			return this;
		}
		
		
		/**
		* Returns the leaderboard excluding the player's social friends
		*/
		public LeaderboardsEntriesRequest setInverseSocial( boolean inverseSocial )
		{
			data.put("inverseSocial", inverseSocial);
			return this;
		}
		
		
		/**
		* The list of leaderboards shortcodes
		*/
		public LeaderboardsEntriesRequest setLeaderboards( List<String> leaderboards )
		{
			data.put("leaderboards", leaderboards);
			return this;
		}
		
		
		/**
		* The player id. Leave out to use the current player id
		*/
		public LeaderboardsEntriesRequest setPlayer( String player )
		{
			data.put("player", player);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public LeaderboardsEntriesRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* Set to true to include the player's game friends
		*/
		public LeaderboardsEntriesRequest setSocial( boolean social )
		{
			data.put("social", social);
			return this;
		}
		
		
		/**
		* The types of team to apply this request to
		*/
		public LeaderboardsEntriesRequest setTeamTypes( List<String> teamTypes )
		{
			data.put("teamTypes", teamTypes);
			return this;
		}
		
		
		
				
	}
	
	
	public LeaveTeamRequest createLeaveTeamRequest(){
		return new LeaveTeamRequest(gs);
	}
	
	public class LeaveTeamRequest extends AbstractRequest<LeaveTeamResponse>
	{
		private LeaveTeamRequest(GS gs) {
			super(gs);
			data.put("@class", ".LeaveTeamRequest");
		}
		
		protected LeaveTeamResponse createResponse(Map data) {
			return responseBuilder.newLeaveTeamResponse(data);
		}
		
		protected LeaveTeamRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The team owner to find, used in combination with teamType. If not supplied the current players id will be used
		*/
		public LeaveTeamRequest setOwnerId( String ownerId )
		{
			data.put("ownerId", ownerId);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public LeaveTeamRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The teamId to find (may be null if teamType supplied)
		*/
		public LeaveTeamRequest setTeamId( String teamId )
		{
			data.put("teamId", teamId);
			return this;
		}
		
		
		/**
		* The teamType to find, used in combination with the current player, or the player defined by ownerId
		*/
		public LeaveTeamRequest setTeamType( String teamType )
		{
			data.put("teamType", teamType);
			return this;
		}
		
		
		
				
	}
	
	
	public ListAchievementsRequest createListAchievementsRequest(){
		return new ListAchievementsRequest(gs);
	}
	
	public class ListAchievementsRequest extends AbstractRequest<ListAchievementsResponse>
	{
		private ListAchievementsRequest(GS gs) {
			super(gs);
			data.put("@class", ".ListAchievementsRequest");
		}
		
		protected ListAchievementsResponse createResponse(Map data) {
			return responseBuilder.newListAchievementsResponse(data);
		}
		
		protected ListAchievementsRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public ListAchievementsRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public ListBulkJobsAdminRequest createListBulkJobsAdminRequest(){
		return new ListBulkJobsAdminRequest(gs);
	}
	
	public class ListBulkJobsAdminRequest extends AbstractRequest<ListBulkJobsAdminResponse>
	{
		private ListBulkJobsAdminRequest(GS gs) {
			super(gs);
			data.put("@class", ".ListBulkJobsAdminRequest");
		}
		
		protected ListBulkJobsAdminResponse createResponse(Map data) {
			return responseBuilder.newListBulkJobsAdminResponse(data);
		}
		
		protected ListBulkJobsAdminRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The IDs of existing bulk jobs to get details for
		*/
		public ListBulkJobsAdminRequest setBulkJobIds( List<String> bulkJobIds )
		{
			data.put("bulkJobIds", bulkJobIds);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public ListBulkJobsAdminRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public ListChallengeRequest createListChallengeRequest(){
		return new ListChallengeRequest(gs);
	}
	
	public class ListChallengeRequest extends AbstractRequest<ListChallengeResponse>
	{
		private ListChallengeRequest(GS gs) {
			super(gs);
			data.put("@class", ".ListChallengeRequest");
		}
		
		protected ListChallengeResponse createResponse(Map data) {
			return responseBuilder.newListChallengeResponse(data);
		}
		
		protected ListChallengeRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The number of items to return in a page (default=50)
		*/
		public ListChallengeRequest setEntryCount( long entryCount )
		{
			data.put("entryCount", entryCount);
			return this;
		}
		
		
		/**
		* The offset (page number) to start from (default=0)
		*/
		public ListChallengeRequest setOffset( long offset )
		{
			data.put("offset", offset);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public ListChallengeRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The type of challenge to return
		*/
		public ListChallengeRequest setShortCode( String shortCode )
		{
			data.put("shortCode", shortCode);
			return this;
		}
		
		
		/**
		* The state of the challenged to be returned
		*/
		public ListChallengeRequest setState( String state )
		{
			data.put("state", state);
			return this;
		}
		
		
		/**
		* The states of the challenges to be returned
		*/
		public ListChallengeRequest setStates( List<String> states )
		{
			data.put("states", states);
			return this;
		}
		
		
		
				
	}
	
	
	public ListChallengeTypeRequest createListChallengeTypeRequest(){
		return new ListChallengeTypeRequest(gs);
	}
	
	public class ListChallengeTypeRequest extends AbstractRequest<ListChallengeTypeResponse>
	{
		private ListChallengeTypeRequest(GS gs) {
			super(gs);
			data.put("@class", ".ListChallengeTypeRequest");
		}
		
		protected ListChallengeTypeResponse createResponse(Map data) {
			return responseBuilder.newListChallengeTypeResponse(data);
		}
		
		protected ListChallengeTypeRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public ListChallengeTypeRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public ListGameFriendsRequest createListGameFriendsRequest(){
		return new ListGameFriendsRequest(gs);
	}
	
	public class ListGameFriendsRequest extends AbstractRequest<ListGameFriendsResponse>
	{
		private ListGameFriendsRequest(GS gs) {
			super(gs);
			data.put("@class", ".ListGameFriendsRequest");
		}
		
		protected ListGameFriendsResponse createResponse(Map data) {
			return responseBuilder.newListGameFriendsResponse(data);
		}
		
		protected ListGameFriendsRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public ListGameFriendsRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public ListInviteFriendsRequest createListInviteFriendsRequest(){
		return new ListInviteFriendsRequest(gs);
	}
	
	public class ListInviteFriendsRequest extends AbstractRequest<ListInviteFriendsResponse>
	{
		private ListInviteFriendsRequest(GS gs) {
			super(gs);
			data.put("@class", ".ListInviteFriendsRequest");
		}
		
		protected ListInviteFriendsResponse createResponse(Map data) {
			return responseBuilder.newListInviteFriendsResponse(data);
		}
		
		protected ListInviteFriendsRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public ListInviteFriendsRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public ListLeaderboardsRequest createListLeaderboardsRequest(){
		return new ListLeaderboardsRequest(gs);
	}
	
	public class ListLeaderboardsRequest extends AbstractRequest<ListLeaderboardsResponse>
	{
		private ListLeaderboardsRequest(GS gs) {
			super(gs);
			data.put("@class", ".ListLeaderboardsRequest");
		}
		
		protected ListLeaderboardsResponse createResponse(Map data) {
			return responseBuilder.newListLeaderboardsResponse(data);
		}
		
		protected ListLeaderboardsRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public ListLeaderboardsRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public ListMessageDetailRequest createListMessageDetailRequest(){
		return new ListMessageDetailRequest(gs);
	}
	
	public class ListMessageDetailRequest extends AbstractRequest<ListMessageDetailResponse>
	{
		private ListMessageDetailRequest(GS gs) {
			super(gs);
			data.put("@class", ".ListMessageDetailRequest");
		}
		
		protected ListMessageDetailResponse createResponse(Map data) {
			return responseBuilder.newListMessageDetailResponse(data);
		}
		
		protected ListMessageDetailRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The number of items to return in a page (default=50)
		*/
		public ListMessageDetailRequest setEntryCount( long entryCount )
		{
			data.put("entryCount", entryCount);
			return this;
		}
		
		
		/**
		* An optional filter that limits the message types returned
		*/
		public ListMessageDetailRequest setInclude( String include )
		{
			data.put("include", include);
			return this;
		}
		
		
		/**
		* The offset (page number) to start from (default=0)
		*/
		public ListMessageDetailRequest setOffset( long offset )
		{
			data.put("offset", offset);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public ListMessageDetailRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The status of messages to be retrieved. If omitted, messages of all statuses will be retrieved
		*/
		public ListMessageDetailRequest setStatus( String status )
		{
			data.put("status", status);
			return this;
		}
		
		
		
				
	}
	
	
	public ListMessageRequest createListMessageRequest(){
		return new ListMessageRequest(gs);
	}
	
	public class ListMessageRequest extends AbstractRequest<ListMessageResponse>
	{
		private ListMessageRequest(GS gs) {
			super(gs);
			data.put("@class", ".ListMessageRequest");
		}
		
		protected ListMessageResponse createResponse(Map data) {
			return responseBuilder.newListMessageResponse(data);
		}
		
		protected ListMessageRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The number of items to return in a page (default=50)
		*/
		public ListMessageRequest setEntryCount( long entryCount )
		{
			data.put("entryCount", entryCount);
			return this;
		}
		
		
		/**
		* An optional filter that limits the message types returned
		*/
		public ListMessageRequest setInclude( String include )
		{
			data.put("include", include);
			return this;
		}
		
		
		/**
		* The offset (page number) to start from (default=0)
		*/
		public ListMessageRequest setOffset( long offset )
		{
			data.put("offset", offset);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public ListMessageRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public ListMessageSummaryRequest createListMessageSummaryRequest(){
		return new ListMessageSummaryRequest(gs);
	}
	
	public class ListMessageSummaryRequest extends AbstractRequest<ListMessageSummaryResponse>
	{
		private ListMessageSummaryRequest(GS gs) {
			super(gs);
			data.put("@class", ".ListMessageSummaryRequest");
		}
		
		protected ListMessageSummaryResponse createResponse(Map data) {
			return responseBuilder.newListMessageSummaryResponse(data);
		}
		
		protected ListMessageSummaryRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The number of items to return in a page (default=50)
		*/
		public ListMessageSummaryRequest setEntryCount( long entryCount )
		{
			data.put("entryCount", entryCount);
			return this;
		}
		
		
		/**
		* The offset (page number) to start from (default=0)
		*/
		public ListMessageSummaryRequest setOffset( long offset )
		{
			data.put("offset", offset);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public ListMessageSummaryRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public ListTeamChatRequest createListTeamChatRequest(){
		return new ListTeamChatRequest(gs);
	}
	
	public class ListTeamChatRequest extends AbstractRequest<ListTeamChatResponse>
	{
		private ListTeamChatRequest(GS gs) {
			super(gs);
			data.put("@class", ".ListTeamChatRequest");
		}
		
		protected ListTeamChatResponse createResponse(Map data) {
			return responseBuilder.newListTeamChatResponse(data);
		}
		
		protected ListTeamChatRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The number of messages to return (default=50)
		*/
		public ListTeamChatRequest setEntryCount( long entryCount )
		{
			data.put("entryCount", entryCount);
			return this;
		}
		
		
		/**
		* The offset (nth message) to start from (default=0)
		*/
		public ListTeamChatRequest setOffset( long offset )
		{
			data.put("offset", offset);
			return this;
		}
		
		
		/**
		* The team owner to find, used in combination with teamType. If not supplied the current players id will be used
		*/
		public ListTeamChatRequest setOwnerId( String ownerId )
		{
			data.put("ownerId", ownerId);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public ListTeamChatRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The teamId to find (may be null if teamType supplied)
		*/
		public ListTeamChatRequest setTeamId( String teamId )
		{
			data.put("teamId", teamId);
			return this;
		}
		
		
		/**
		* The teamType to find, used in combination with the current player, or the player defined by ownerId
		*/
		public ListTeamChatRequest setTeamType( String teamType )
		{
			data.put("teamType", teamType);
			return this;
		}
		
		
		
				
	}
	
	
	public ListTransactionsRequest createListTransactionsRequest(){
		return new ListTransactionsRequest(gs);
	}
	
	public class ListTransactionsRequest extends AbstractRequest<ListTransactionsResponse>
	{
		private ListTransactionsRequest(GS gs) {
			super(gs);
			data.put("@class", ".ListTransactionsRequest");
		}
		
		protected ListTransactionsResponse createResponse(Map data) {
			return responseBuilder.newListTransactionsResponse(data);
		}
		
		protected ListTransactionsRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* Optional date constraint to list transactions from
		*/
		public ListTransactionsRequest setDateFrom( Date dateFrom )
		{
			setDateField("dateFrom", dateFrom);
			return this;
		}
		
		
		/**
		* Optional date constraint to list transactions to
		*/
		public ListTransactionsRequest setDateTo( Date dateTo )
		{
			setDateField("dateTo", dateTo);
			return this;
		}
		
		
		/**
		* The number of items to return in a page (default=50)
		*/
		public ListTransactionsRequest setEntryCount( long entryCount )
		{
			data.put("entryCount", entryCount);
			return this;
		}
		
		
		/**
		* An optional filter that limits the transaction types returned
		*/
		public ListTransactionsRequest setInclude( String include )
		{
			data.put("include", include);
			return this;
		}
		
		
		/**
		* The offset (page number) to start from (default=0)
		*/
		public ListTransactionsRequest setOffset( long offset )
		{
			data.put("offset", offset);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public ListTransactionsRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public ListVirtualGoodsRequest createListVirtualGoodsRequest(){
		return new ListVirtualGoodsRequest(gs);
	}
	
	public class ListVirtualGoodsRequest extends AbstractRequest<ListVirtualGoodsResponse>
	{
		private ListVirtualGoodsRequest(GS gs) {
			super(gs);
			data.put("@class", ".ListVirtualGoodsRequest");
		}
		
		protected ListVirtualGoodsResponse createResponse(Map data) {
			return responseBuilder.newListVirtualGoodsResponse(data);
		}
		
		protected ListVirtualGoodsRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* If true, the returned list will include disabled VirtualVoods
		*/
		public ListVirtualGoodsRequest setIncludeDisabled( boolean includeDisabled )
		{
			data.put("includeDisabled", includeDisabled);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public ListVirtualGoodsRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* A filter to only include goods with the given tags. Each good must have all the provided tags.
		*/
		public ListVirtualGoodsRequest setTags( List<String> tags )
		{
			data.put("tags", tags);
			return this;
		}
		
		
		
				
	}
	
	
	public LogChallengeEventRequest createLogChallengeEventRequest(){
		return new LogChallengeEventRequest(gs);
	}
	
	public class LogChallengeEventRequest extends AbstractRequest<LogChallengeEventResponse>
	{
		private LogChallengeEventRequest(GS gs) {
			super(gs);
			data.put("@class", ".LogChallengeEventRequest");
		}
		
		protected LogChallengeEventResponse createResponse(Map data) {
			return responseBuilder.newLogChallengeEventResponse(data);
		}
		
		protected LogChallengeEventRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The ID challenge instance to target
		*/
		public LogChallengeEventRequest setChallengeInstanceId( String challengeInstanceId )
		{
			data.put("challengeInstanceId", challengeInstanceId);
			return this;
		}
		
		
		/**
		* The short code of the event to trigger
		*/
		public LogChallengeEventRequest setEventKey( String eventKey )
		{
			data.put("eventKey", eventKey);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public LogChallengeEventRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
		public LogChallengeEventRequest setEventAttribute(String shortCode, Number value){
			data.put(shortCode, value);
			return this;
		}
		
		public LogChallengeEventRequest setEventAttribute(String shortCode, String value){
			data.put(shortCode, value);
			return this;
		}
		
		public LogChallengeEventRequest setEventAttribute(String shortCode, GSData value){
			data.put(shortCode, value.getBaseData());
			return this;
		}
		
				
	}
	
	
	public LogEventRequest createLogEventRequest(){
		return new LogEventRequest(gs);
	}
	
	public class LogEventRequest extends AbstractRequest<LogEventResponse>
	{
		private LogEventRequest(GS gs) {
			super(gs);
			data.put("@class", ".LogEventRequest");
		}
		
		protected LogEventResponse createResponse(Map data) {
			return responseBuilder.newLogEventResponse(data);
		}
		
		protected LogEventRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The short code of the event to trigger
		*/
		public LogEventRequest setEventKey( String eventKey )
		{
			data.put("eventKey", eventKey);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public LogEventRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
		public LogEventRequest setEventAttribute(String shortCode, Number value){
			data.put(shortCode, value);
			return this;
		}
		
		public LogEventRequest setEventAttribute(String shortCode, String value){
			data.put(shortCode, value);
			return this;
		}
		
		public LogEventRequest setEventAttribute(String shortCode, GSData value){
			data.put(shortCode, value.getBaseData());
			return this;
		}
		
				
	}
	
	
	public MatchDetailsRequest createMatchDetailsRequest(){
		return new MatchDetailsRequest(gs);
	}
	
	public class MatchDetailsRequest extends AbstractRequest<MatchDetailsResponse>
	{
		private MatchDetailsRequest(GS gs) {
			super(gs);
			data.put("@class", ".MatchDetailsRequest");
		}
		
		protected MatchDetailsResponse createResponse(Map data) {
			return responseBuilder.newMatchDetailsResponse(data);
		}
		
		protected MatchDetailsRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The matchId to find the details of
		*/
		public MatchDetailsRequest setMatchId( String matchId )
		{
			data.put("matchId", matchId);
			return this;
		}
		
		
		/**
		* Adds realtime server details if the match has been created using Cloud Code and it has not been realtime enabled
		*/
		public MatchDetailsRequest setRealtimeEnabled( boolean realtimeEnabled )
		{
			data.put("realtimeEnabled", realtimeEnabled);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public MatchDetailsRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public MatchmakingRequest createMatchmakingRequest(){
		return new MatchmakingRequest(gs);
	}
	
	public class MatchmakingRequest extends AbstractRequest<MatchmakingResponse>
	{
		private MatchmakingRequest(GS gs) {
			super(gs);
			data.put("@class", ".MatchmakingRequest");
		}
		
		protected MatchmakingResponse createResponse(Map data) {
			return responseBuilder.newMatchmakingResponse(data);
		}
		
		protected MatchmakingRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The action to take on the already in-flight request for this match. Currently supported actions are: 'cancel'
		*/
		public MatchmakingRequest setAction( String action )
		{
			data.put("action", action);
			return this;
		}
		
		
		/**
		* The query that will be applied to the PendingMatch collection
		*/
		public MatchmakingRequest setCustomQuery( Map<String,Object> customQuery )
		{
			data.put("customQuery", customQuery);
			return this;
		}
		
		
		/**
		* A JSON Map of any data that will be associated to the pending match
		*/
		public MatchmakingRequest setMatchData( Map<String,Object> matchData )
		{
			data.put("matchData", matchData);
			return this;
		}
		
		
		/**
		* Optional. Players will be grouped based on the distinct value passed in here, only players in the same group can be matched together
		*/
		public MatchmakingRequest setMatchGroup( String matchGroup )
		{
			data.put("matchGroup", matchGroup);
			return this;
		}
		
		
		/**
		* The shortCode of the match type this player is registering for
		*/
		public MatchmakingRequest setMatchShortCode( String matchShortCode )
		{
			data.put("matchShortCode", matchShortCode);
			return this;
		}
		
		
		/**
		* A JSON Map of any data that will be associated to this user in a pending match
		*/
		public MatchmakingRequest setParticipantData( Map<String,Object> participantData )
		{
			data.put("participantData", participantData);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public MatchmakingRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The skill of the player looking for a match
		*/
		public MatchmakingRequest setSkill( long skill )
		{
			data.put("skill", skill);
			return this;
		}
		
		
		
				
	}
	
	
	public PSNConnectRequest createPSNConnectRequest(){
		return new PSNConnectRequest(gs);
	}
	
	public class PSNConnectRequest extends AbstractRequest<AuthenticationResponse>
	{
		private PSNConnectRequest(GS gs) {
			super(gs);
			data.put("@class", ".PSNConnectRequest");
		}
		
		protected AuthenticationResponse createResponse(Map data) {
			return responseBuilder.newAuthenticationResponse(data);
		}
		
		protected PSNConnectRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The authorization code obtained from PSN, as described here https://ps4.scedev.net/resources/documents/SDK/latest/NpAuth-Reference/0008.html
		*/
		public PSNConnectRequest setAuthorizationCode( String authorizationCode )
		{
			data.put("authorizationCode", authorizationCode);
			return this;
		}
		
		
		/**
		* Indicates that the server should not try to link the external profile with the current player.  If false, links the external profile to the currently signed in player.  If true, creates a new player and links the external profile to them.  Defaults to false.
		*/
		public PSNConnectRequest setDoNotLinkToCurrentPlayer( boolean doNotLinkToCurrentPlayer )
		{
			data.put("doNotLinkToCurrentPlayer", doNotLinkToCurrentPlayer);
			return this;
		}
		
		
		/**
		* Indicates whether the server should return an error if an account switch would have occurred, rather than switching automatically.  Defaults to false.
		*/
		public PSNConnectRequest setErrorOnSwitch( boolean errorOnSwitch )
		{
			data.put("errorOnSwitch", errorOnSwitch);
			return this;
		}
		
		
		/**
		* When using the authorization code obtained from PlayStation®4/PlayStation®Vita/PlayStation®3, this is not required.
		* When using the authorization code obtained with the PC authentication gateway, set the URI issued from the Developer Network website.
		*/
		public PSNConnectRequest setRedirectUri( String redirectUri )
		{
			data.put("redirectUri", redirectUri);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public PSNConnectRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* An optional segment configuration for this request.
		* If this request creates a new player on the gamesparks platform, the segments of the new player will match the values provided
		*/
		public PSNConnectRequest setSegments( Map<String,Object> segments )
		{
			data.put("segments", segments);
			return this;
		}
		
		
		/**
		* Indicates that the server should switch to the supplied profile if it isalready associated to a player. Defaults to false.
		*/
		public PSNConnectRequest setSwitchIfPossible( boolean switchIfPossible )
		{
			data.put("switchIfPossible", switchIfPossible);
			return this;
		}
		
		
		/**
		* Indicates that the associated players displayName should be kept in syn with this profile when it's updated by the external provider.
		*/
		public PSNConnectRequest setSyncDisplayName( boolean syncDisplayName )
		{
			data.put("syncDisplayName", syncDisplayName);
			return this;
		}
		
		
		
				
	}
	
	
	public PsnBuyGoodsRequest createPsnBuyGoodsRequest(){
		return new PsnBuyGoodsRequest(gs);
	}
	
	public class PsnBuyGoodsRequest extends AbstractRequest<BuyVirtualGoodResponse>
	{
		private PsnBuyGoodsRequest(GS gs) {
			super(gs);
			data.put("@class", ".PsnBuyGoodsRequest");
		}
		
		protected BuyVirtualGoodResponse createResponse(Map data) {
			return responseBuilder.newBuyVirtualGoodResponse(data);
		}
		
		protected PsnBuyGoodsRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The authorization code obtained from PSN, as described here https://ps4.scedev.net/resources/documents/SDK/latest/NpAuth-Reference/0008.html
		*/
		public PsnBuyGoodsRequest setAuthorizationCode( String authorizationCode )
		{
			data.put("authorizationCode", authorizationCode);
			return this;
		}
		
		
		/**
		* Specify the entitlement label of the entitlement to update. (Not an entitlement ID).
		*/
		public PsnBuyGoodsRequest setEntitlementLabel( String entitlementLabel )
		{
			data.put("entitlementLabel", entitlementLabel);
			return this;
		}
		
		
		/**
		* When using the authorization code obtained from PlayStation®4/PlayStation®Vita/PlayStation®3, this is not required.
		* When using the authorization code obtained with the PC authentication gateway, set the URI issued from the Developer Network website.
		*/
		public PsnBuyGoodsRequest setRedirectUri( String redirectUri )
		{
			data.put("redirectUri", redirectUri);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public PsnBuyGoodsRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* If set to true, the transactionId from this receipt will not be globally valdidated, this will mean replays between players are possible.
		* It will only validate the transactionId has not been used by this player before.
		*/
		public PsnBuyGoodsRequest setUniqueTransactionByPlayer( boolean uniqueTransactionByPlayer )
		{
			data.put("uniqueTransactionByPlayer", uniqueTransactionByPlayer);
			return this;
		}
		
		
		/**
		* Optional - specify the quantity of the entitlement to use. Default = 1
		*/
		public PsnBuyGoodsRequest setUseCount( long useCount )
		{
			data.put("useCount", useCount);
			return this;
		}
		
		
		
				
	}
	
	
	public PushRegistrationRequest createPushRegistrationRequest(){
		return new PushRegistrationRequest(gs);
	}
	
	public class PushRegistrationRequest extends AbstractRequest<PushRegistrationResponse>
	{
		private PushRegistrationRequest(GS gs) {
			super(gs);
			data.put("@class", ".PushRegistrationRequest");
		}
		
		protected PushRegistrationResponse createResponse(Map data) {
			return responseBuilder.newPushRegistrationResponse(data);
		}
		
		protected PushRegistrationRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The type of id, valid values are ios, android, fcm, wp8, w8, kindle or viber
		*/
		public PushRegistrationRequest setDeviceOS( String deviceOS )
		{
			data.put("deviceOS", deviceOS);
			return this;
		}
		
		
		/**
		* The push notification identifier for the device
		*/
		public PushRegistrationRequest setPushId( String pushId )
		{
			data.put("pushId", pushId);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public PushRegistrationRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public QQConnectRequest createQQConnectRequest(){
		return new QQConnectRequest(gs);
	}
	
	public class QQConnectRequest extends AbstractRequest<AuthenticationResponse>
	{
		private QQConnectRequest(GS gs) {
			super(gs);
			data.put("@class", ".QQConnectRequest");
		}
		
		protected AuthenticationResponse createResponse(Map data) {
			return responseBuilder.newAuthenticationResponse(data);
		}
		
		protected QQConnectRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The access token is used by the client to make authenticated requests on behalf of the end user.
		* When the access token expires, attempts to use it will fail, and a new access token must be obtained using the refresh token.
		*/
		public QQConnectRequest setAccessToken( String accessToken )
		{
			data.put("accessToken", accessToken);
			return this;
		}
		
		
		/**
		* Indicates that the server should not try to link the external profile with the current player.  If false, links the external profile to the currently signed in player.  If true, creates a new player and links the external profile to them.  Defaults to false.
		*/
		public QQConnectRequest setDoNotLinkToCurrentPlayer( boolean doNotLinkToCurrentPlayer )
		{
			data.put("doNotLinkToCurrentPlayer", doNotLinkToCurrentPlayer);
			return this;
		}
		
		
		/**
		* Indicates whether the server should return an error if an account switch would have occurred, rather than switching automatically.  Defaults to false.
		*/
		public QQConnectRequest setErrorOnSwitch( boolean errorOnSwitch )
		{
			data.put("errorOnSwitch", errorOnSwitch);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public QQConnectRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* An optional segment configuration for this request.
		* If this request creates a new player on the gamesparks platform, the segments of the new player will match the values provided
		*/
		public QQConnectRequest setSegments( Map<String,Object> segments )
		{
			data.put("segments", segments);
			return this;
		}
		
		
		/**
		* Indicates that the server should switch to the supplied profile if it isalready associated to a player. Defaults to false.
		*/
		public QQConnectRequest setSwitchIfPossible( boolean switchIfPossible )
		{
			data.put("switchIfPossible", switchIfPossible);
			return this;
		}
		
		
		/**
		* Indicates that the associated players displayName should be kept in syn with this profile when it's updated by the external provider.
		*/
		public QQConnectRequest setSyncDisplayName( boolean syncDisplayName )
		{
			data.put("syncDisplayName", syncDisplayName);
			return this;
		}
		
		
		
				
	}
	
	
	public RegistrationRequest createRegistrationRequest(){
		return new RegistrationRequest(gs);
	}
	
	public class RegistrationRequest extends AbstractRequest<RegistrationResponse>
	{
		private RegistrationRequest(GS gs) {
			super(gs);
			data.put("@class", ".RegistrationRequest");
		}
		
		protected RegistrationResponse createResponse(Map data) {
			return responseBuilder.newRegistrationResponse(data);
		}
		
		protected RegistrationRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* A display name to use
		*/
		public RegistrationRequest setDisplayName( String displayName )
		{
			data.put("displayName", displayName);
			return this;
		}
		
		
		/**
		* The previously registered password
		*/
		public RegistrationRequest setPassword( String password )
		{
			data.put("password", password);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public RegistrationRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* An optional segment configuration for this request.
		* If this request creates a new player on the gamesparks platform, the segments of the new player will match the values provided
		*/
		public RegistrationRequest setSegments( Map<String,Object> segments )
		{
			data.put("segments", segments);
			return this;
		}
		
		
		/**
		* The previously registered player name
		*/
		public RegistrationRequest setUserName( String userName )
		{
			data.put("userName", userName);
			return this;
		}
		
		
		
				
	}
	
	
	public RevokePurchaseGoodsRequest createRevokePurchaseGoodsRequest(){
		return new RevokePurchaseGoodsRequest(gs);
	}
	
	public class RevokePurchaseGoodsRequest extends AbstractRequest<RevokePurchaseGoodsResponse>
	{
		private RevokePurchaseGoodsRequest(GS gs) {
			super(gs);
			data.put("@class", ".RevokePurchaseGoodsRequest");
		}
		
		protected RevokePurchaseGoodsResponse createResponse(Map data) {
			return responseBuilder.newRevokePurchaseGoodsResponse(data);
		}
		
		protected RevokePurchaseGoodsRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public RevokePurchaseGoodsRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The store type for which to revoke these transactions
		*/
		public RevokePurchaseGoodsRequest setStoreType( String storeType )
		{
			data.put("storeType", storeType);
			return this;
		}
		
		
		/**
		* The list of transactionIds to revoke
		*/
		public RevokePurchaseGoodsRequest setTransactionIds( List<String> transactionIds )
		{
			data.put("transactionIds", transactionIds);
			return this;
		}
		
		
		
				
	}
	
	
	public ScheduleBulkJobAdminRequest createScheduleBulkJobAdminRequest(){
		return new ScheduleBulkJobAdminRequest(gs);
	}
	
	public class ScheduleBulkJobAdminRequest extends AbstractRequest<ScheduleBulkJobAdminResponse>
	{
		private ScheduleBulkJobAdminRequest(GS gs) {
			super(gs);
			data.put("@class", ".ScheduleBulkJobAdminRequest");
		}
		
		protected ScheduleBulkJobAdminResponse createResponse(Map data) {
			return responseBuilder.newScheduleBulkJobAdminResponse(data);
		}
		
		protected ScheduleBulkJobAdminRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* Optional data to be passed into the script
		*/
		public ScheduleBulkJobAdminRequest setData( Map<String,Object> data )
		{
			data.put("data", data);
			return this;
		}
		
		
		/**
		* The short code of the cloud code module to be executed against each player
		*/
		public ScheduleBulkJobAdminRequest setModuleShortCode( String moduleShortCode )
		{
			data.put("moduleShortCode", moduleShortCode);
			return this;
		}
		
		
		/**
		* The query to be run against the player collection to identify which players to execute the cloud code for
		*/
		public ScheduleBulkJobAdminRequest setPlayerQuery( Map<String,Object> playerQuery )
		{
			data.put("playerQuery", playerQuery);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public ScheduleBulkJobAdminRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* An optional date and time for this job to be run
		*/
		public ScheduleBulkJobAdminRequest setScheduledTime( Date scheduledTime )
		{
			setDateField("scheduledTime", scheduledTime);
			return this;
		}
		
		
		/**
		* The script to be executed against each player
		*/
		public ScheduleBulkJobAdminRequest setScript( String script )
		{
			data.put("script", script);
			return this;
		}
		
		
		
				
	}
	
	
	public SendFriendMessageRequest createSendFriendMessageRequest(){
		return new SendFriendMessageRequest(gs);
	}
	
	public class SendFriendMessageRequest extends AbstractRequest<SendFriendMessageResponse>
	{
		private SendFriendMessageRequest(GS gs) {
			super(gs);
			data.put("@class", ".SendFriendMessageRequest");
		}
		
		protected SendFriendMessageResponse createResponse(Map data) {
			return responseBuilder.newSendFriendMessageResponse(data);
		}
		
		protected SendFriendMessageRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* One or more friend ID's. This can be supplied as a single string, or a JSON array
		*/
		public SendFriendMessageRequest setFriendIds( List<String> friendIds )
		{
			data.put("friendIds", friendIds);
			return this;
		}
		
		
		/**
		* The message to send
		*/
		public SendFriendMessageRequest setMessage( String message )
		{
			data.put("message", message);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public SendFriendMessageRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public SendTeamChatMessageRequest createSendTeamChatMessageRequest(){
		return new SendTeamChatMessageRequest(gs);
	}
	
	public class SendTeamChatMessageRequest extends AbstractRequest<SendTeamChatMessageResponse>
	{
		private SendTeamChatMessageRequest(GS gs) {
			super(gs);
			data.put("@class", ".SendTeamChatMessageRequest");
		}
		
		protected SendTeamChatMessageResponse createResponse(Map data) {
			return responseBuilder.newSendTeamChatMessageResponse(data);
		}
		
		protected SendTeamChatMessageRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The message to send
		*/
		public SendTeamChatMessageRequest setMessage( String message )
		{
			data.put("message", message);
			return this;
		}
		
		
		/**
		* The team owner to find, used in combination with teamType. If not supplied the current players id will be used
		*/
		public SendTeamChatMessageRequest setOwnerId( String ownerId )
		{
			data.put("ownerId", ownerId);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public SendTeamChatMessageRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The teamId to find (may be null if teamType supplied)
		*/
		public SendTeamChatMessageRequest setTeamId( String teamId )
		{
			data.put("teamId", teamId);
			return this;
		}
		
		
		/**
		* The teamType to find, used in combination with the current player, or the player defined by ownerId
		*/
		public SendTeamChatMessageRequest setTeamType( String teamType )
		{
			data.put("teamType", teamType);
			return this;
		}
		
		
		
				
	}
	
	
	public SocialDisconnectRequest createSocialDisconnectRequest(){
		return new SocialDisconnectRequest(gs);
	}
	
	public class SocialDisconnectRequest extends AbstractRequest<SocialDisconnectResponse>
	{
		private SocialDisconnectRequest(GS gs) {
			super(gs);
			data.put("@class", ".SocialDisconnectRequest");
		}
		
		protected SocialDisconnectResponse createResponse(Map data) {
			return responseBuilder.newSocialDisconnectResponse(data);
		}
		
		protected SocialDisconnectRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public SocialDisconnectRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The external system from which to disconnect this profile, supplied as a two letter ID. The options are: {FACEBOOK:FB, AMAZON:AM, GAME_CENTER:GC
		* GOOGLE_PLAY:GY , GOOGLE_PLUS:GP, KONGREGATE:KO, PSN:PS, QQ:QQ, STEAM:ST, TWITCH:TC, TWITTER:TW, VIBER:VB, WECHAT:WC, XBOX:XB}
		*/
		public SocialDisconnectRequest setSystemId( String systemId )
		{
			data.put("systemId", systemId);
			return this;
		}
		
		
		
				
	}
	
	
	public SocialLeaderboardDataRequest createSocialLeaderboardDataRequest(){
		return new SocialLeaderboardDataRequest(gs);
	}
	
	public class SocialLeaderboardDataRequest extends AbstractRequest<LeaderboardDataResponse>
	{
		private SocialLeaderboardDataRequest(GS gs) {
			super(gs);
			data.put("@class", ".SocialLeaderboardDataRequest");
		}
		
		protected LeaderboardDataResponse createResponse(Map data) {
			return responseBuilder.newLeaderboardDataResponse(data);
		}
		
		protected SocialLeaderboardDataRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The challenge instance to get the leaderboard data for
		*/
		public SocialLeaderboardDataRequest setChallengeInstanceId( String challengeInstanceId )
		{
			data.put("challengeInstanceId", challengeInstanceId);
			return this;
		}
		
		
		/**
		* The default behaviour on a social request is to error if the player has no friends (NOTSOCIAL).  Set this flag to suppress that error and return the player's leaderboard entry instead.
		*/
		public SocialLeaderboardDataRequest setDontErrorOnNotSocial( boolean dontErrorOnNotSocial )
		{
			data.put("dontErrorOnNotSocial", dontErrorOnNotSocial);
			return this;
		}
		
		
		/**
		* The number of items to return in a page (default=50)
		*/
		public SocialLeaderboardDataRequest setEntryCount( long entryCount )
		{
			data.put("entryCount", entryCount);
			return this;
		}
		
		
		/**
		* A friend id or an array of friend ids to use instead of the player's social friends
		*/
		public SocialLeaderboardDataRequest setFriendIds( List<String> friendIds )
		{
			data.put("friendIds", friendIds);
			return this;
		}
		
		
		/**
		* Number of entries to include from head of the list
		*/
		public SocialLeaderboardDataRequest setIncludeFirst( long includeFirst )
		{
			data.put("includeFirst", includeFirst);
			return this;
		}
		
		
		/**
		* Number of entries to include from tail of the list
		*/
		public SocialLeaderboardDataRequest setIncludeLast( long includeLast )
		{
			data.put("includeLast", includeLast);
			return this;
		}
		
		
		/**
		* Returns the leaderboard excluding the player's social friends
		*/
		public SocialLeaderboardDataRequest setInverseSocial( boolean inverseSocial )
		{
			data.put("inverseSocial", inverseSocial);
			return this;
		}
		
		
		/**
		* The short code of the leaderboard
		*/
		public SocialLeaderboardDataRequest setLeaderboardShortCode( String leaderboardShortCode )
		{
			data.put("leaderboardShortCode", leaderboardShortCode);
			return this;
		}
		
		
		/**
		* The offset into the set of leaderboards returned
		*/
		public SocialLeaderboardDataRequest setOffset( long offset )
		{
			data.put("offset", offset);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public SocialLeaderboardDataRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* If True returns a leaderboard of the player's social friends
		*/
		public SocialLeaderboardDataRequest setSocial( boolean social )
		{
			data.put("social", social);
			return this;
		}
		
		
		/**
		* The IDs of the teams you are interested in
		*/
		public SocialLeaderboardDataRequest setTeamIds( List<String> teamIds )
		{
			data.put("teamIds", teamIds);
			return this;
		}
		
		
		/**
		* The type of team you are interested in
		*/
		public SocialLeaderboardDataRequest setTeamTypes( List<String> teamTypes )
		{
			data.put("teamTypes", teamTypes);
			return this;
		}
		
		
		
				
	}
	
	
	public SocialStatusRequest createSocialStatusRequest(){
		return new SocialStatusRequest(gs);
	}
	
	public class SocialStatusRequest extends AbstractRequest<SocialStatusResponse>
	{
		private SocialStatusRequest(GS gs) {
			super(gs);
			data.put("@class", ".SocialStatusRequest");
		}
		
		protected SocialStatusResponse createResponse(Map data) {
			return responseBuilder.newSocialStatusResponse(data);
		}
		
		protected SocialStatusRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public SocialStatusRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public SteamBuyGoodsRequest createSteamBuyGoodsRequest(){
		return new SteamBuyGoodsRequest(gs);
	}
	
	public class SteamBuyGoodsRequest extends AbstractRequest<BuyVirtualGoodResponse>
	{
		private SteamBuyGoodsRequest(GS gs) {
			super(gs);
			data.put("@class", ".SteamBuyGoodsRequest");
		}
		
		protected BuyVirtualGoodResponse createResponse(Map data) {
			return responseBuilder.newBuyVirtualGoodResponse(data);
		}
		
		protected SteamBuyGoodsRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* Unique 64-bit ID for order
		*/
		public SteamBuyGoodsRequest setOrderId( String orderId )
		{
			data.put("orderId", orderId);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public SteamBuyGoodsRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* If set to true, the transactionId from this receipt will not be globally valdidated, this will mean replays between players are possible.
		* It will only validate the transactionId has not been used by this player before.
		*/
		public SteamBuyGoodsRequest setUniqueTransactionByPlayer( boolean uniqueTransactionByPlayer )
		{
			data.put("uniqueTransactionByPlayer", uniqueTransactionByPlayer);
			return this;
		}
		
		
		
				
	}
	
	
	public SteamConnectRequest createSteamConnectRequest(){
		return new SteamConnectRequest(gs);
	}
	
	public class SteamConnectRequest extends AbstractRequest<AuthenticationResponse>
	{
		private SteamConnectRequest(GS gs) {
			super(gs);
			data.put("@class", ".SteamConnectRequest");
		}
		
		protected AuthenticationResponse createResponse(Map data) {
			return responseBuilder.newAuthenticationResponse(data);
		}
		
		protected SteamConnectRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* Indicates that the server should not try to link the external profile with the current player.  If false, links the external profile to the currently signed in player.  If true, creates a new player and links the external profile to them.  Defaults to false.
		*/
		public SteamConnectRequest setDoNotLinkToCurrentPlayer( boolean doNotLinkToCurrentPlayer )
		{
			data.put("doNotLinkToCurrentPlayer", doNotLinkToCurrentPlayer);
			return this;
		}
		
		
		/**
		* Indicates whether the server should return an error if an account switch would have occurred, rather than switching automatically.  Defaults to false.
		*/
		public SteamConnectRequest setErrorOnSwitch( boolean errorOnSwitch )
		{
			data.put("errorOnSwitch", errorOnSwitch);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public SteamConnectRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* An optional segment configuration for this request.
		* If this request creates a new player on the gamesparks platform, the segments of the new player will match the values provided
		*/
		public SteamConnectRequest setSegments( Map<String,Object> segments )
		{
			data.put("segments", segments);
			return this;
		}
		
		
		/**
		* The hex encoded UTF-8 string representation of the ticket acquired calling the Steam SDKs GetAuthSessionTicket.
		*/
		public SteamConnectRequest setSessionTicket( String sessionTicket )
		{
			data.put("sessionTicket", sessionTicket);
			return this;
		}
		
		
		/**
		* Indicates that the server should switch to the supplied profile if it isalready associated to a player. Defaults to false.
		*/
		public SteamConnectRequest setSwitchIfPossible( boolean switchIfPossible )
		{
			data.put("switchIfPossible", switchIfPossible);
			return this;
		}
		
		
		/**
		* Indicates that the associated players displayName should be kept in syn with this profile when it's updated by the external provider.
		*/
		public SteamConnectRequest setSyncDisplayName( boolean syncDisplayName )
		{
			data.put("syncDisplayName", syncDisplayName);
			return this;
		}
		
		
		
				
	}
	
	
	public TwitchConnectRequest createTwitchConnectRequest(){
		return new TwitchConnectRequest(gs);
	}
	
	public class TwitchConnectRequest extends AbstractRequest<AuthenticationResponse>
	{
		private TwitchConnectRequest(GS gs) {
			super(gs);
			data.put("@class", ".TwitchConnectRequest");
		}
		
		protected AuthenticationResponse createResponse(Map data) {
			return responseBuilder.newAuthenticationResponse(data);
		}
		
		protected TwitchConnectRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The access token is used by the client to make authenticated requests on behalf of the end user.
		*/
		public TwitchConnectRequest setAccessToken( String accessToken )
		{
			data.put("accessToken", accessToken);
			return this;
		}
		
		
		/**
		* Indicates that the server should not try to link the external profile with the current player.  If false, links the external profile to the currently signed in player.  If true, creates a new player and links the external profile to them.  Defaults to false.
		*/
		public TwitchConnectRequest setDoNotLinkToCurrentPlayer( boolean doNotLinkToCurrentPlayer )
		{
			data.put("doNotLinkToCurrentPlayer", doNotLinkToCurrentPlayer);
			return this;
		}
		
		
		/**
		* Indicates whether the server should return an error if an account switch would have occurred, rather than switching automatically.  Defaults to false.
		*/
		public TwitchConnectRequest setErrorOnSwitch( boolean errorOnSwitch )
		{
			data.put("errorOnSwitch", errorOnSwitch);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public TwitchConnectRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* An optional segment configuration for this request.
		* If this request creates a new player on the gamesparks platform, the segments of the new player will match the values provided
		*/
		public TwitchConnectRequest setSegments( Map<String,Object> segments )
		{
			data.put("segments", segments);
			return this;
		}
		
		
		/**
		* Indicates that the server should switch to the supplied profile if it isalready associated to a player. Defaults to false.
		*/
		public TwitchConnectRequest setSwitchIfPossible( boolean switchIfPossible )
		{
			data.put("switchIfPossible", switchIfPossible);
			return this;
		}
		
		
		/**
		* Indicates that the associated players displayName should be kept in syn with this profile when it's updated by the external provider.
		*/
		public TwitchConnectRequest setSyncDisplayName( boolean syncDisplayName )
		{
			data.put("syncDisplayName", syncDisplayName);
			return this;
		}
		
		
		
				
	}
	
	
	public TwitterConnectRequest createTwitterConnectRequest(){
		return new TwitterConnectRequest(gs);
	}
	
	public class TwitterConnectRequest extends AbstractRequest<AuthenticationResponse>
	{
		private TwitterConnectRequest(GS gs) {
			super(gs);
			data.put("@class", ".TwitterConnectRequest");
		}
		
		protected AuthenticationResponse createResponse(Map data) {
			return responseBuilder.newAuthenticationResponse(data);
		}
		
		protected TwitterConnectRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The accessSecret is obtained at the same time as the accessToken, and is required to sign requests to Twitter's services that require the accessToken.
		*/
		public TwitterConnectRequest setAccessSecret( String accessSecret )
		{
			data.put("accessSecret", accessSecret);
			return this;
		}
		
		
		/**
		* The accessToken represents a player's permission to share access to their account with your application.
		* To obtain an accessToken for the player see https://dev.twitter.com/docs/auth/obtaining-access-tokens.
		* Currently, Twitter accessTokens do not expire but they can be revoked by the player.
		*/
		public TwitterConnectRequest setAccessToken( String accessToken )
		{
			data.put("accessToken", accessToken);
			return this;
		}
		
		
		/**
		* Indicates that the server should not try to link the external profile with the current player.  If false, links the external profile to the currently signed in player.  If true, creates a new player and links the external profile to them.  Defaults to false.
		*/
		public TwitterConnectRequest setDoNotLinkToCurrentPlayer( boolean doNotLinkToCurrentPlayer )
		{
			data.put("doNotLinkToCurrentPlayer", doNotLinkToCurrentPlayer);
			return this;
		}
		
		
		/**
		* Indicates whether the server should return an error if an account switch would have occurred, rather than switching automatically.  Defaults to false.
		*/
		public TwitterConnectRequest setErrorOnSwitch( boolean errorOnSwitch )
		{
			data.put("errorOnSwitch", errorOnSwitch);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public TwitterConnectRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* An optional segment configuration for this request.
		* If this request creates a new player on the gamesparks platform, the segments of the new player will match the values provided
		*/
		public TwitterConnectRequest setSegments( Map<String,Object> segments )
		{
			data.put("segments", segments);
			return this;
		}
		
		
		/**
		* Indicates that the server should switch to the supplied profile if it isalready associated to a player. Defaults to false.
		*/
		public TwitterConnectRequest setSwitchIfPossible( boolean switchIfPossible )
		{
			data.put("switchIfPossible", switchIfPossible);
			return this;
		}
		
		
		/**
		* Indicates that the associated players displayName should be kept in syn with this profile when it's updated by the external provider.
		*/
		public TwitterConnectRequest setSyncDisplayName( boolean syncDisplayName )
		{
			data.put("syncDisplayName", syncDisplayName);
			return this;
		}
		
		
		
				
	}
	
	
	public UpdateMessageRequest createUpdateMessageRequest(){
		return new UpdateMessageRequest(gs);
	}
	
	public class UpdateMessageRequest extends AbstractRequest<UpdateMessageResponse>
	{
		private UpdateMessageRequest(GS gs) {
			super(gs);
			data.put("@class", ".UpdateMessageRequest");
		}
		
		protected UpdateMessageResponse createResponse(Map data) {
			return responseBuilder.newUpdateMessageResponse(data);
		}
		
		protected UpdateMessageRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The messageId of the message to update
		*/
		public UpdateMessageRequest setMessageId( String messageId )
		{
			data.put("messageId", messageId);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public UpdateMessageRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The status to set on the message
		*/
		public UpdateMessageRequest setStatus( String status )
		{
			data.put("status", status);
			return this;
		}
		
		
		
				
	}
	
	
	public ViberConnectRequest createViberConnectRequest(){
		return new ViberConnectRequest(gs);
	}
	
	public class ViberConnectRequest extends AbstractRequest<AuthenticationResponse>
	{
		private ViberConnectRequest(GS gs) {
			super(gs);
			data.put("@class", ".ViberConnectRequest");
		}
		
		protected AuthenticationResponse createResponse(Map data) {
			return responseBuilder.newAuthenticationResponse(data);
		}
		
		protected ViberConnectRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The accessToken represents a player's permission to share access to their account with your application.
		*/
		public ViberConnectRequest setAccessToken( String accessToken )
		{
			data.put("accessToken", accessToken);
			return this;
		}
		
		
		/**
		* Indicates that the server should not try to link the external profile with the current player.  If false, links the external profile to the currently signed in player.  If true, creates a new player and links the external profile to them.  Defaults to false.
		*/
		public ViberConnectRequest setDoNotLinkToCurrentPlayer( boolean doNotLinkToCurrentPlayer )
		{
			data.put("doNotLinkToCurrentPlayer", doNotLinkToCurrentPlayer);
			return this;
		}
		
		
		/**
		* Does not automatocally register this user for push notifications. Defaults to false.
		*/
		public ViberConnectRequest setDoNotRegisterForPush( boolean doNotRegisterForPush )
		{
			data.put("doNotRegisterForPush", doNotRegisterForPush);
			return this;
		}
		
		
		/**
		* Indicates whether the server should return an error if an account switch would have occurred, rather than switching automatically.  Defaults to false.
		*/
		public ViberConnectRequest setErrorOnSwitch( boolean errorOnSwitch )
		{
			data.put("errorOnSwitch", errorOnSwitch);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public ViberConnectRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* An optional segment configuration for this request.
		* If this request creates a new player on the gamesparks platform, the segments of the new player will match the values provided
		*/
		public ViberConnectRequest setSegments( Map<String,Object> segments )
		{
			data.put("segments", segments);
			return this;
		}
		
		
		/**
		* Indicates that the server should switch to the supplied profile if it isalready associated to a player. Defaults to false.
		*/
		public ViberConnectRequest setSwitchIfPossible( boolean switchIfPossible )
		{
			data.put("switchIfPossible", switchIfPossible);
			return this;
		}
		
		
		/**
		* Indicates that the associated players displayName should be kept in syn with this profile when it's updated by the external provider.
		*/
		public ViberConnectRequest setSyncDisplayName( boolean syncDisplayName )
		{
			data.put("syncDisplayName", syncDisplayName);
			return this;
		}
		
		
		
				
	}
	
	
	public WeChatConnectRequest createWeChatConnectRequest(){
		return new WeChatConnectRequest(gs);
	}
	
	public class WeChatConnectRequest extends AbstractRequest<AuthenticationResponse>
	{
		private WeChatConnectRequest(GS gs) {
			super(gs);
			data.put("@class", ".WeChatConnectRequest");
		}
		
		protected AuthenticationResponse createResponse(Map data) {
			return responseBuilder.newAuthenticationResponse(data);
		}
		
		protected WeChatConnectRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The access token sould be obtained from WeChat
		* It is used by the client to make authenticated requests on behalf of the end user.
		*/
		public WeChatConnectRequest setAccessToken( String accessToken )
		{
			data.put("accessToken", accessToken);
			return this;
		}
		
		
		/**
		* Indicates that the server should not try to link the external profile with the current player.  If false, links the external profile to the currently signed in player.  If true, creates a new player and links the external profile to them.  Defaults to false.
		*/
		public WeChatConnectRequest setDoNotLinkToCurrentPlayer( boolean doNotLinkToCurrentPlayer )
		{
			data.put("doNotLinkToCurrentPlayer", doNotLinkToCurrentPlayer);
			return this;
		}
		
		
		/**
		* Indicates whether the server should return an error if an account switch would have occurred, rather than switching automatically.  Defaults to false.
		*/
		public WeChatConnectRequest setErrorOnSwitch( boolean errorOnSwitch )
		{
			data.put("errorOnSwitch", errorOnSwitch);
			return this;
		}
		
		
		/**
		* The open ID corresponding to the WeChat user
		*/
		public WeChatConnectRequest setOpenId( String openId )
		{
			data.put("openId", openId);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public WeChatConnectRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* An optional segment configuration for this request.
		* If this request creates a new player on the gamesparks platform, the segments of the new player will match the values provided
		*/
		public WeChatConnectRequest setSegments( Map<String,Object> segments )
		{
			data.put("segments", segments);
			return this;
		}
		
		
		/**
		* Indicates that the server should switch to the supplied profile if it isalready associated to a player. Defaults to false.
		*/
		public WeChatConnectRequest setSwitchIfPossible( boolean switchIfPossible )
		{
			data.put("switchIfPossible", switchIfPossible);
			return this;
		}
		
		
		/**
		* Indicates that the associated players displayName should be kept in syn with this profile when it's updated by the external provider.
		*/
		public WeChatConnectRequest setSyncDisplayName( boolean syncDisplayName )
		{
			data.put("syncDisplayName", syncDisplayName);
			return this;
		}
		
		
		
				
	}
	
	
	public WindowsBuyGoodsRequest createWindowsBuyGoodsRequest(){
		return new WindowsBuyGoodsRequest(gs);
	}
	
	public class WindowsBuyGoodsRequest extends AbstractRequest<BuyVirtualGoodResponse>
	{
		private WindowsBuyGoodsRequest(GS gs) {
			super(gs);
			data.put("@class", ".WindowsBuyGoodsRequest");
		}
		
		protected BuyVirtualGoodResponse createResponse(Map data) {
			return responseBuilder.newBuyVirtualGoodResponse(data);
		}
		
		protected WindowsBuyGoodsRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* Allows you to specify the platform
		*/
		public WindowsBuyGoodsRequest setPlatform( String platform )
		{
			data.put("platform", platform);
			return this;
		}
		
		
		/**
		* The xml reciept returned from the windows phone 8 store
		*/
		public WindowsBuyGoodsRequest setReceipt( String receipt )
		{
			data.put("receipt", receipt);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public WindowsBuyGoodsRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* If set to true, the transactionId from this receipt will not be globally valdidated, this will mean replays between players are possible.
		* It will only validate the transactionId has not been used by this player before.
		*/
		public WindowsBuyGoodsRequest setUniqueTransactionByPlayer( boolean uniqueTransactionByPlayer )
		{
			data.put("uniqueTransactionByPlayer", uniqueTransactionByPlayer);
			return this;
		}
		
		
		
				
	}
	
	
	public WithdrawChallengeRequest createWithdrawChallengeRequest(){
		return new WithdrawChallengeRequest(gs);
	}
	
	public class WithdrawChallengeRequest extends AbstractRequest<WithdrawChallengeResponse>
	{
		private WithdrawChallengeRequest(GS gs) {
			super(gs);
			data.put("@class", ".WithdrawChallengeRequest");
		}
		
		protected WithdrawChallengeResponse createResponse(Map data) {
			return responseBuilder.newWithdrawChallengeResponse(data);
		}
		
		protected WithdrawChallengeRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The ID of the challenge
		*/
		public WithdrawChallengeRequest setChallengeInstanceId( String challengeInstanceId )
		{
			data.put("challengeInstanceId", challengeInstanceId);
			return this;
		}
		
		
		/**
		* An optional message to send with the challenge
		*/
		public WithdrawChallengeRequest setMessage( String message )
		{
			data.put("message", message);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public WithdrawChallengeRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		
				
	}
	
	
	public XBOXLiveConnectRequest createXBOXLiveConnectRequest(){
		return new XBOXLiveConnectRequest(gs);
	}
	
	public class XBOXLiveConnectRequest extends AbstractRequest<AuthenticationResponse>
	{
		private XBOXLiveConnectRequest(GS gs) {
			super(gs);
			data.put("@class", ".XBOXLiveConnectRequest");
		}
		
		protected AuthenticationResponse createResponse(Map data) {
			return responseBuilder.newAuthenticationResponse(data);
		}
		
		protected XBOXLiveConnectRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* The displayName to set for the player in GameSparks
		*/
		public XBOXLiveConnectRequest setDisplayName( String displayName )
		{
			data.put("displayName", displayName);
			return this;
		}
		
		
		/**
		* Indicates that the server should not try to link the external profile with the current player.  If false, links the external profile to the currently signed in player.  If true, creates a new player and links the external profile to them.  Defaults to false.
		*/
		public XBOXLiveConnectRequest setDoNotLinkToCurrentPlayer( boolean doNotLinkToCurrentPlayer )
		{
			data.put("doNotLinkToCurrentPlayer", doNotLinkToCurrentPlayer);
			return this;
		}
		
		
		/**
		* Indicates whether the server should return an error if an account switch would have occurred, rather than switching automatically.  Defaults to false.
		*/
		public XBOXLiveConnectRequest setErrorOnSwitch( boolean errorOnSwitch )
		{
			data.put("errorOnSwitch", errorOnSwitch);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public XBOXLiveConnectRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* An optional segment configuration for this request.
		* If this request creates a new player on the gamesparks platform, the segments of the new player will match the values provided
		*/
		public XBOXLiveConnectRequest setSegments( Map<String,Object> segments )
		{
			data.put("segments", segments);
			return this;
		}
		
		
		/**
		* The access token is used by the client to make authenticated requests on behalf of the end user.
		* It has a longer lifetime than the authorization code, typically on the order of minutes or hours.
		* When the access token expires, attempts to use it will fail, and a new access token must be obtained via a refresh token.
		*/
		public XBOXLiveConnectRequest setStsTokenString( String stsTokenString )
		{
			data.put("stsTokenString", stsTokenString);
			return this;
		}
		
		
		/**
		* Indicates that the server should switch to the supplied profile if it isalready associated to a player. Defaults to false.
		*/
		public XBOXLiveConnectRequest setSwitchIfPossible( boolean switchIfPossible )
		{
			data.put("switchIfPossible", switchIfPossible);
			return this;
		}
		
		
		/**
		* Indicates that the associated players displayName should be kept in syn with this profile when it's updated by the external provider.
		*/
		public XBOXLiveConnectRequest setSyncDisplayName( boolean syncDisplayName )
		{
			data.put("syncDisplayName", syncDisplayName);
			return this;
		}
		
		
		
				
	}
	
	
	public XboxOneConnectRequest createXboxOneConnectRequest(){
		return new XboxOneConnectRequest(gs);
	}
	
	public class XboxOneConnectRequest extends AbstractRequest<AuthenticationResponse>
	{
		private XboxOneConnectRequest(GS gs) {
			super(gs);
			data.put("@class", ".XboxOneConnectRequest");
		}
		
		protected AuthenticationResponse createResponse(Map data) {
			return responseBuilder.newAuthenticationResponse(data);
		}
		
		protected XboxOneConnectRequest setPlayerId(String playerId)
		{
			data.put("playerId", playerId);
			return this;
		}
		
		/**
		* Indicates that the server should not try to link the external profile with the current player.  If false, links the external profile to the currently signed in player.  If true, creates a new player and links the external profile to them.  Defaults to false.
		*/
		public XboxOneConnectRequest setDoNotLinkToCurrentPlayer( boolean doNotLinkToCurrentPlayer )
		{
			data.put("doNotLinkToCurrentPlayer", doNotLinkToCurrentPlayer);
			return this;
		}
		
		
		/**
		* Indicates whether the server should return an error if an account switch would have occurred, rather than switching automatically.  Defaults to false.
		*/
		public XboxOneConnectRequest setErrorOnSwitch( boolean errorOnSwitch )
		{
			data.put("errorOnSwitch", errorOnSwitch);
			return this;
		}
		
		
		/**
		* The SDK adds a requestId to all requests, this is used to match responses from the websocket
		*/
		public XboxOneConnectRequest setRequestId( String requestId )
		{
			data.put("requestId", requestId);
			return this;
		}
		
		
		/**
		* The Xbox Live sandbox to use. If not specified, the sandbox from the decoded token will be used.
		*/
		public XboxOneConnectRequest setSandbox( String sandbox )
		{
			data.put("sandbox", sandbox);
			return this;
		}
		
		
		/**
		* An optional segment configuration for this request.
		* If this request creates a new player on the gamesparks platform, the segments of the new player will match the values provided
		*/
		public XboxOneConnectRequest setSegments( Map<String,Object> segments )
		{
			data.put("segments", segments);
			return this;
		}
		
		
		/**
		* Indicates that the server should switch to the supplied profile if it isalready associated to a player. Defaults to false.
		*/
		public XboxOneConnectRequest setSwitchIfPossible( boolean switchIfPossible )
		{
			data.put("switchIfPossible", switchIfPossible);
			return this;
		}
		
		
		/**
		* Indicates that the associated players displayName should be kept in syn with this profile when it's updated by the external provider.
		*/
		public XboxOneConnectRequest setSyncDisplayName( boolean syncDisplayName )
		{
			data.put("syncDisplayName", syncDisplayName);
			return this;
		}
		
		
		/**
		* The Xbox One authentication token
		*/
		public XboxOneConnectRequest setToken( String token )
		{
			data.put("token", token);
			return this;
		}
		
		
		
				
	}
	
	
}


