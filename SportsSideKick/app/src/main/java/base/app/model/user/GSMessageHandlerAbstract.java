package base.app.model.user;

import com.gamesparks.sdk.api.autogen.GSMessageHandler;

import java.util.Map;

/**
 * Created by Filip on 3/13/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public abstract class GSMessageHandlerAbstract {

    public void onMessage(Map<String, Object> data){}

    // -- General
    public void onGSScriptMessage(String type, Map<String, Object> data){}

    // -- State
    public void onGSSessionTerminatedMessage(GSMessageHandler.SessionTerminatedMessage message){}

    // -- File-upload
    public void onGSUploadCompleteMessage(GSMessageHandler.UploadCompleteMessage message){}

    // -- Friends
    public void onGSFriendMessage(GSMessageHandler.FriendMessage message){}

    // -- Achievements
    public void onGSAchievementEarnedMessage(GSMessageHandler.AchievementEarnedMessage message){}

    // -- Challenges
    public void onGSChallengeAcceptedMessage(GSMessageHandler.ChallengeAcceptedMessage message){}

    public void onGSChallengeChangedMessage(GSMessageHandler.ChallengeChangedMessage message){}

    public void onGSChallengeChatMessage(GSMessageHandler.ChallengeChatMessage message){}

    public void onGSChallengeDeclinedMessage(GSMessageHandler.ChallengeDeclinedMessage message){}

    public void onGSChallengeDrawnMessage(GSMessageHandler.ChallengeDrawnMessage message){}

    public void onGSChallengeExpiredMessage(GSMessageHandler.ChallengeExpiredMessage message){}

    public void onGSChallengeIssuedMessage(GSMessageHandler.ChallengeIssuedMessage message){}

    public void onGSChallengeJoinedMessage(GSMessageHandler.ChallengeJoinedMessage message){}

    public void onGSChallengeLapsedMessage(GSMessageHandler.ChallengeLapsedMessage message){}

    public void onGSChallengeLostMessage(GSMessageHandler.ChallengeLostMessage message){}

    public void onGSChallengeStartedMessage(GSMessageHandler.ChallengeStartedMessage message){}

    public void onGSChallengeTurnTakenMessage(GSMessageHandler.ChallengeTurnTakenMessage message){}

    public void onGSChallengeWaitingMessage(GSMessageHandler.ChallengeWaitingMessage message){}

    public void onGSChallengeWithdrawnMessage(GSMessageHandler.ChallengeWithdrawnMessage message){}

    public void onGSChallengeWonMessage(GSMessageHandler.ChallengeWonMessage message){}

    // -- Teams and Ranking
    public void onGSGlobalRankChangedMessage(GSMessageHandler.GlobalRankChangedMessage message){}

    public void onGSTeamChatMessage(GSMessageHandler.TeamChatMessage message){}

    public void onGSTeamRankChangedMessage(GSMessageHandler.TeamRankChangedMessage message){}

    public void onGSSocialRankChangedMessage(GSMessageHandler.SocialRankChangedMessage message){}

    public void onGSNewHighScoreMessage(GSMessageHandler.NewHighScoreMessage message){}

    public void onGSNewTeamScoreMessage(GSMessageHandler.NewTeamScoreMessage message){}

    // -- Matches
    public void onGSMatchFoundMessage(GSMessageHandler.MatchFoundMessage message){}

    public void onGSMatchNotFoundMessage(GSMessageHandler.MatchNotFoundMessage message){}

    public void onGSMatchUpdatedMessage(GSMessageHandler.MatchUpdatedMessage message){}
}
