package Monster;

import Monster.monster.Circusboy;
import Monster.monster.Darkmash;
import Monster.monster.Dekamash;
import Monster.monster.Eggman;
import Monster.monster.Makaimash;
import Monster.monster.Mash;
import Monster.monster.Metalmash;
import Monster.monster.Shibiremash;
import Monster.monster.Togemash;

public class Monster {
    //全モンスターの数
    public static final int MONSTERNUM = 9;

    //モンスターナンバー
    public int MONSTERNUMBER;

    //モンスターの名前
    public String NAME;

    //レベルの最大値
    public int MAXLV;

    //レベルごとの能力
    public int
        MAXHP[],
        MAXSP[],
        ATTACK[],
        DEFENCE[],
        TECH[],
        SPEED[],
        EXP[],   //次のレベルまでに必要な経験値
        DROPEXP[],   //倒したときに獲得する経験値
        DROPMAONEY[], //敵として戦ったときの報酬
        ESCAPEPERCENT;


    //覚えるスキル
    public int SKILL[];
    public int LVFORSKILL[];

    //動的なステータス
    public int
        LV,
        HP,
        SP,
        GETEXP; //今獲得している経験値

    public static Monster MonsterOutput(int monsternumber, int level) {
        switch (monsternumber) {
            case 1:
                Mash mash = new Mash(monsternumber, level);
                return mash;
            case 2:
                Togemash togemash = new Togemash(monsternumber, level);
                return togemash;
            case 3:
                Darkmash darkmash = new Darkmash(monsternumber, level);
                return darkmash;
            case 4:
                Metalmash metalmash = new Metalmash(monsternumber, level);
                return metalmash;
            case 5:
                Shibiremash shibiremash = new Shibiremash(monsternumber, level);
                return shibiremash;
            case 6:
                Makaimash makaimash = new Makaimash(monsternumber, level);
                return makaimash;
            case 7:
                Dekamash dekamash = new Dekamash(monsternumber, level);
                return dekamash;
            case 8:
                Eggman eggman = new Eggman(monsternumber, level);
                return eggman;
            case 9:
                Circusboy circusboy = new Circusboy(monsternumber, level);
                return circusboy;
            default:
        }
        return null;
    }
}


