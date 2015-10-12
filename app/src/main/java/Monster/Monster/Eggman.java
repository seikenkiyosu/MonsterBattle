package Monster.monster;

import Monster.Monster;

/**
 * Created by seikenkiyosu on 15/10/11.
 */
public class Eggman extends Monster {
    private final int maxlv = 10;	//最大レベル
    private String name = "エッグマン";	//名前
    private int
            maxHP[]      = {0,  6, 10, 14, 18, 22, 26, 30, 34, 38, 42},	//最大HP
            maxSP[]      = {0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10},	//最大SP
            attack[]     = {0,  2,  4,  6,  8, 10, 12, 14, 16, 18, 20},	//攻撃力
            defence[]    = {0,  2,  4,  6,  8, 10, 12, 14, 16, 18, 20},	//防御力
            tech[]       = {0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0},//きようさ
            speed[]      = {0,  1,  3,  4,  6,  7,  9, 10, 12, 13, 15},	//スピード
            exp[]        = {0,  3,  5,  8, 11, 13, 15, 18, 20, 23, 25},	//次のレベルまでの経験値
            dropexp[]    = {0,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11},	//倒されたときに落とす経験値
            dropmoney[]  = {0,  2,  4,  6,  8, 10, 12, 14, 16, 18, 20};	//倒されたときに落とす金
    private int
            skill[]      = {0},		//レベルごとに覚えるスキル
            lvforskill[] = {0};		//そのスキルを覚えるレベル

    public Eggman(int monsternumber, int level) {
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
        SKILL = new int[MAXLV+1];
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

