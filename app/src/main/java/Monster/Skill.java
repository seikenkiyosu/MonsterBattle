package Monster;

import com.test.seikenkiyosu.monsterbattle.RPGView;

public class Skill {
    //スキル名
    public static String NAME[] = {
            "",
            "アンパンチ",
            "アンキック",
            "キック",
            "アンドライブ",
            "はかいこうせん",
            "ギガインパクト"
    };

    //スキル発動
    public static void Skillcast(RPGView rpg, int skillnumber, Monster attacker, Monster defender) {
        switch (skillnumber) {
            case 1:
                Anpanch(rpg, attacker, defender);
                break;
            case 2:
                Anchop(rpg, attacker, defender);
                break;
            case 3:
                Kick(rpg, attacker, defender);
                break;
            case 4:
                Ankick(rpg, attacker, defender);
                break;
            case 5:
                Hakaikousen(rpg, attacker, defender);
                break;
            case 6:
                Gigaimpact(rpg, attacker, defender);
            default:
        }
    }

    /*スキル番号ごとにスキル定義*/
    //1
    private static void Anpanch(RPGView rpg, Monster attacker, Monster defender) {
        int sp = 1;
        if (attacker.SP - sp >= 0) {   //SPが足りたら
            //スキル消費
            attacker.SP -= sp;
            //エフェクト
            for (int i = 0; i < 20; i++) {
                rpg.drawBattleFlush(attacker.NAME + "のアンパンチ!", i % 2 == 0, rpg.enemytarget);
                rpg.sleep(50);
            }
            //効果
            int damage = 10;
            rpg.drawBattle(defender.NAME + "に", damage + "ダメージ!", true);
            rpg.waitSelect();
            defender.HP -= damage;
            if (defender.HP <= 0) {
                rpg.drawBattle(defender.NAME + "を倒した");
                rpg.waitSelect();
                defender.HP = 0;
            }

        } else {  //SPが足りない場合
            rpg.drawBattle("SPが足りない!");
            rpg.waitSelect();
        }
    }

    //2
    private static void Anchop(RPGView rpg, Monster attacker, Monster defender) {
        int sp = 1;
        if (attacker.SP - sp >= 0) {   //SPが足りたら
            //スキル消費
            attacker.SP -= sp;
            //エフェクト
            for (int i = 0; i < 20; i++) {
                rpg.drawBattleFlush(attacker.NAME + "のアンチョップ!", i % 2 == 0, rpg.enemytarget);
                rpg.sleep(50);
            }
            //効果
            int damage = 20;
            rpg.drawBattle(defender.NAME + "に", damage + "ダメージ!", true);
            rpg.waitSelect();
            defender.HP -= damage;
            if (defender.HP <= 0) {
                rpg.drawBattle(defender.NAME + "を倒した");
                rpg.waitSelect();
                defender.HP = 0;
            }
        } else {  //SPが足りない場合
            rpg.drawBattle("SPが足りない!");
            rpg.init = rpg.S_COMMAND;
            rpg.waitSelect();
        }
    }

    //3
    private static void Kick(RPGView rpg, Monster attacker, Monster defender) {
        int sp = 4;
        if (attacker.SP - sp >= 0) {   //SPが足りたら
            //スキル消費
            attacker.SP -= sp;
            //エフェクト
            for (int i = 0; i < 20; i++) {
                rpg.drawBattleFlush(attacker.NAME + "のキック!", i % 2 == 0, rpg.enemytarget);
                rpg.sleep(50);
            }
            //効果
            int damage = 30;
            rpg.drawBattle(defender.NAME + "に", damage + "ダメージ!", true);
            rpg.waitSelect();
            defender.HP -= damage;
            if (defender.HP <= 0) {
                rpg.drawBattle(defender.NAME + "を倒した");
                rpg.waitSelect();
                defender.HP = 0;
            }
        } else {  //SPが足りない場合
            rpg.drawBattle("SPが足りない!");
            rpg.init = rpg.S_COMMAND;
            rpg.waitSelect();
        }
    }

    //4
    private static void Ankick(RPGView rpg, Monster attacker, Monster defender) {
        int sp = 4;
        if (attacker.SP - sp >= 0) {   //SPが足りたら
            //スキル消費
            attacker.SP -= sp;
            //エフェクト
            for (int i = 0; i < 20; i++) {
                rpg.drawBattleFlush(attacker.NAME + "のアンキック!", i % 2 == 0, rpg.enemytarget);
                rpg.sleep(50);
            }
            //効果
            int damage = 40;
            rpg.drawBattle(defender.NAME + "に", damage + "ダメージを与えた", true);
            rpg.waitSelect();
            defender.HP -= damage;
            if (defender.HP <= 0) {
                rpg.drawBattle(defender.NAME + "を倒した");
                rpg.waitSelect();
                defender.HP = 0;
            }
        } else {  //SPが足りない場合
            rpg.drawBattle("SPが足りない!");
            rpg.init = rpg.S_COMMAND;
            rpg.waitSelect();
        }
    }

    private static void Hakaikousen (RPGView rpg, Monster attacker, Monster defender) {
        int sp = 1;
        if (attacker.SP - sp >= 0) {   //SPが足りたら
            //スキル消費
            attacker.SP -= sp;
            //エフェクト
            for (int i = 0; i < 20; i++) {
                rpg.drawBattleFlush(attacker.NAME + "のはかいこうせん!", i % 2 == 0, rpg.enemytarget);
                rpg.sleep(50);
            }
            //効果
            int damage = 1000;
            rpg.drawBattle(defender.NAME + "に", damage + "ダメージを与えた", true);
            rpg.waitSelect();
            defender.HP -= damage;
            if (defender.HP <= 0) {
                rpg.drawBattle(defender.NAME + "を倒した");
                rpg.waitSelect();
                defender.HP = 0;
            }
        } else {  //SPが足りない場合
            rpg.drawBattle("SPが足りない!");
            rpg.init = rpg.S_COMMAND;
            rpg.waitSelect();
        }
    }

    private static void Gigaimpact (RPGView rpg, Monster attacker, Monster defender) {
        int sp = 1;
        if (attacker.SP - sp >= 0) {   //SPが足りたら
            //スキル消費
            attacker.SP -= sp;
            //エフェクト
            for (int i = 0; i < 20; i++) {
                rpg.drawBattleFlush(attacker.NAME + "のギガインパクト!", i % 2 == 0, rpg.enemytarget);
                rpg.sleep(50);
            }
            //効果
            int damage = 1000;
            rpg.drawBattle(defender.NAME + "に", damage + "ダメージを与えた", true);
            rpg.waitSelect();
            defender.HP -= damage;
            if (defender.HP <= 0) {
                rpg.drawBattle(defender.NAME + "を倒した");
                rpg.waitSelect();
                defender.HP = 0;
            }
        } else {  //SPが足りない場合
            rpg.drawBattle("SPが足りない!");
            rpg.init = rpg.S_COMMAND;
            rpg.waitSelect();
        }
    }
}
