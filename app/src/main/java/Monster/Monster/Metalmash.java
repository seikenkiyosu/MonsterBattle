package Monster.monster;

import Monster.Monster;

public class Metalmash extends Monster {
    private final int maxlv = 1;
    private String name = "メタルマッシュ";
    private int
            maxHP[]    = {0,2},
            maxSP[]    = {0,5},
            attack[]   = {0,5},
            defence[]  = {0,100},
            tech[]       = {0,  0,  0,  0,  0,  0},
            speed[]    = {0,100},
            exp[]      = {0,5},
            dropexp[]  = {0,30},
            dropmoney[]  = {0,1};
    private int
            skill[]      = {1},
            lvforskill[] = {2};

    public Metalmash(int monsternumber, int level) {
        MONSTERNUMBER = monsternumber;
        NAME = name;
        MAXLV = maxlv;
        LV = level;

        MAXHP = new int[MAXLV+1];
        MAXSP = new int[MAXLV+1];
        ATTACK = new int[MAXLV+1];
        DEFENCE = new int[MAXLV+1];
        TECH = new int[MAXLV+1];
        SPEED = new int[MAXLV+1];
        EXP = new int[MAXLV+1];
        DROPEXP = new int[MAXLV+1];
        DROPMAONEY = new int[MAXLV+1];
        SKILL = new int[4];
        LVFORSKILL = new int[MAXLV+1];
        for (int i = 0; i <= MAXLV; i++) {
            MAXHP[i] = maxHP[i];
            MAXSP[i] = maxSP[i];
            ATTACK[i] = attack[i];
            DEFENCE[i] = defence[i];
            TECH[i] = tech[i];
            SPEED[i] = speed[i];
            EXP[i] = exp[i];
            DROPEXP[i] = dropexp[i];
            DROPMAONEY[i] = dropmoney[i];
        }

        for (int i = 0; i < skill.length; i++) {
            SKILL[i] = skill[i];
            LVFORSKILL[i] = lvforskill[i];
        }

        HP = MAXHP[LV];
        SP = MAXSP[LV];
    }
}
