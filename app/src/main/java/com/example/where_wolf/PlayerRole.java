package com.example.where_wolf;

public class PlayerRole {
    // Private fields.
    // String sName defines the client in terms of a personal name.
    String sName;
    // int iId defines the client in terms that may be easier for a computer to understand.
    int iId;
    // boolean bAlive defines if the player is alive and can participate in the game.
    boolean bAlive;
    // int iFaction defines the factions that a client can have
    // 0 = Werewolf, 1 = Villager (No abilities), 2 = Medic, 3 = Seer
    int iFaction;
    // int iPowerTarget is a role action being used on the character referenced by iId
    int iPowerTarget;
    // int iAccusationTarget is a role action being used on the character referenced by iId
    int iAccusationTarget;
    // int iVoteTarget is a role action being used on the character referenced by iId
    int iVoteTarget;

    PlayerRole(String sName,
               int iId,
               boolean bAlive,
               int iFaction,
               int iPowerTarget,
               int iAccusationTarget,
               int iVoteTarget) {
        sName             = sName;
        iId               = iId;
        bAlive            = bAlive;
        iFaction          = iFaction;
        iPowerTarget      = iPowerTarget;
        iAccusationTarget = iAccusationTarget;
        iVoteTarget       = iVoteTarget;
    }

}