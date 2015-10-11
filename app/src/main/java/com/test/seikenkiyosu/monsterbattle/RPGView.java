package com.test.seikenkiyosu.monsterbattle;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.Random;

import Field.Field;
import Monster.*;

public class RPGView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    //シーン定数
    public final static int
        S_START      = 0,
        S_MAP        = 1,
        S_APPEAR     = 2,
        S_TURNCHANGE = 3,
        S_COMMAND    = 4,
        S_ATTACK     = 5,
        S_SKILL      = 6,
        S_DEFENCE    = 7,
        S_ESCAPE     = 8;

    //the size of display
    private final static int
        W = 800,
        H = 480;

    //key constant
    private final static int
        KEY_NONE     = -1,
        KEY_LEFT     = 0,
        KEY_RIGHT    = 1,
        KEY_UP       = 2,
        KEY_DOWN     = 3,
        KEY_1        = 4,
        KEY_2        = 5,
        KEY_3        = 6,
        KEY_4        = 7,
        KEY_REDO     = 8,
        KEY_MONSTER1 = 9,
        KEY_MONSTER2 = 10,
        KEY_MONSTER3 = 11,
        KEY_MONSTER4 = 12,
        KEY_SELECT   = 13;

    private boolean isdefence[];
    private boolean isskill = false;
    private boolean isstatushide = false;
    private boolean isinbattle = false;

    private int battleturn = 0;
    int order[];
    int speed[];

    private int stage[];

    //味方モンスター
    static Monster party[] = new Monster[4];  //味方
    private int partytarget;    //アクションを起こしている味方
    private int
        positionX,
        positionY;

    //敵モンスター
    static Monster enemy[] = new Monster[4];  //アクションを起こしている敵
    private int enemytarget;

    //マップ
    private int MAP[][];

    //system
    private SurfaceHolder holder;
    private Graphics      g;
    private Thread        thread;
    public int            init = S_START;
    public int            scene;
    public int            key;
    private Bitmap        bmparroykey;  //十字キーのためのビットマップ
    private Bitmap[]      bmpmaps;      //マップのためのビットマップ
    private Bitmap[]      bmpmonster;   //モンスターのためのビットマップ

    //持っているお金
    private int Money = 0;

    //コンストラクタ
    public RPGView(Activity activity) {
        super(activity);
        //パーティ読み込み
        party[0] = Monster.MonsterOutput(1, 1);
        party[1] = Monster.MonsterOutput(1, 1);
        party[2] = Monster.MonsterOutput(1, 1);
        party[3] = Monster.MonsterOutput(1, 3);

        //最初のステージ
        stage = new int[2];
        stage[0] = 1;
        stage[1] = 2;


        //map読み込み
        MAP = new int[Field.map[stage[0]-1][stage[1]-1].length][];
        for (int i = 0; i < Field.map[stage[0]-1][stage[1]-1].length; i++) {
            MAP[i] = new int[Field.map[stage[0]-1][stage[1]-1][i].length];
            for (int j = 0; j < Field.map[stage[0]-1][stage[1]-1][i].length; j++) {
                MAP[i][j] = Field.map[stage[0]-1][stage[1]-1][i][j];
            }
        }

        //bitmap読み込み
        bmparroykey = readBitmap(activity, "arroykey");
        bmpmaps = new Bitmap[Field.MAPKINDNUM];
        bmpmonster = new Bitmap[Monster.MONSTERNUM+1];
        for (int i = 0; i < Field.MAPKINDNUM; i++) {
            bmpmaps[i] = readBitmap(activity, "map"+i);
        }
        for (int i = 1; i <= Monster.MONSTERNUM; i++) {
            bmpmonster[i] = readBitmap(activity, "monster"+i);
        }

        //generate surface folder
        holder = getHolder();
        holder.setFormat(PixelFormat.RGBA_8888);
        holder.addCallback(this);

        //display size setting
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        int dw = H * p.x/p.y;
        holder.setFixedSize(dw, H);

        //generate Graphics
        g = new Graphics(holder);
        g.setOrigin((dw-W)/2, 0);
    }


    //サーフェイス生成
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new Thread(this);
        thread.start();
    }

    //サーフェイス変更
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    //サーフェイス終了
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread = null;
    }


    //スレッドの処理
    public void run() {
        while(thread != null) {
            //init scene
            if (init >= 0) {
                scene = init;
                //start(yuのステータスは死んだときに意味がある)
                if(scene == S_START) {
                    scene = S_MAP;
                    positionX   = 1;
                    positionY   = 2;
                    //パーティ全回復
                    for (int i = 0; i < party.length; i++) {
                        if (party[i] != null) {
                            party[i].HP = party[i].MAXHP[party[i].LV];
                            party[i].SP = party[i].MAXSP[party[i].LV];
                        }
                    }
                }
                init = -1;
                key = KEY_NONE;
            }

            //map
            switch (scene) {
                case S_MAP:
                    //マップ移動
                    boolean flag = false;
                    if (key == KEY_UP) {    //勇者の移動
                        if (0<=positionY-1) {
                            if (MAP[positionY - 1][positionX] <= 2) {     //上
                                positionY--;
                                flag = true;
                            }
                        }
                    } else if (key == KEY_DOWN) {
                        if (positionY+1<=MAP.length-1)
                        if (MAP[positionY+1][positionX] <= 2) {      //下
                            positionY++;
                            flag = true;
                        }
                    } else if (key == KEY_LEFT) {   //左
                        if (0<=positionX-1) {
                            if (MAP[positionY][positionX-1] <= 2) {
                                positionX--;
                                flag = true;
                            }
                        }
                    } else if (key == KEY_RIGHT) {        //右
                        if (positionX+1 <= MAP[positionY].length-1) {
                            if (MAP[positionY][positionX+1] <= 2) {
                                positionX++;
                                flag = true;
                            }
                        }
                    }

                    //マップ上のイベント
                    if (flag) {
                        if (MAP[positionY][positionX] == 0 && rand(100) < 10) {     //モンスター出現
                            int r = rand(100);
                            init = S_APPEAR;
                        } else if (MAP[positionY][positionX] == 1) {    //宿屋
                            for (int i = 0; i < party.length; i++) {
                                if (party[i] != null) {
                                    party[i].HP = party[i].MAXHP[party[i].LV];
                                    party[i].SP = party[i].MAXSP[party[i].LV];
                                }
                            }
                        } else if (MAP[positionY][positionX] == 2) {    //ボス出現
                            enemy[0] = Monster.MonsterOutput(3, 1);
                            init = S_APPEAR;
                        }
                    }

                    //マップ描画
                    if (init != S_APPEAR) {
                        g.lock();
                        for (int j = -3; j <= 3; j++) {
                            for (int i = -5; i <= 5; i++) {
                                int idx = 3;
                                if (0 <= positionX+i && positionX+i < MAP[0].length && 0 <= positionY+j && positionY+j < MAP.length) {
                                    idx = MAP[positionY+j][positionX+i];    //フィールド内部を描画
                                }
                                g.drawBitmap(bmpmaps[idx], W/2-40+80*i, H/2-40+80*j);   //周りの木を描画
                            }
                        }
                        g.drawMonsterInMap(bmpmonster[party[0].MONSTERNUMBER], W/2-40, H/2-40);
                        g.drawBitmap(bmparroykey, W/2-40+80*(-4), H/2-40+80);       //マップ上に十字キー描画
                        mapStatus();
                        g.unlock();

                    }
                    break;

                //モンスター出現
                case S_APPEAR:
                    //なにが出てきたか
                    enemy = new Monster[4];
                    enemy[0] = Monster.MonsterOutput(7, 1);
                    enemy[1] = Monster.MonsterOutput(5, 1);
                    enemy[2] = Monster.MonsterOutput(8, 1);
                    enemy[3] = Monster.MonsterOutput(9, 1);

                    //戦闘前のフラッシュ
                    isinbattle = true;  //バトルスタート
                    sleep(300);
                    for (int i = 0; i <= 6; i++) {
                        g.lock();
                        if (i % 2 == 0) {
                            g.setColor(Color.rgb(0, 0, 0));
                        } else {
                            g.setColor(Color.rgb(255, 255, 255));
                        }
                        g.fillRect(0, 0, W, H);
                        g.unlock();
                        sleep(100);
                    }

                    //message
                    drawBattle(enemy[0].NAME + "たちが現れた");
                    waitSelect();

                    //防御のための変数用意
                    isdefence = new boolean[party.length];
                    for (int i = 0; i < party.length; i++){ isdefence[i] = false; }

                    //はじめの設定(forすばやさ)
                    battleturn = 0;
                    speed = new int[party.length+enemy.length];
                    order = new int[party.length+enemy.length];
                    for (int i = 0; i < party.length+enemy.length; i++) {
                        if (i < party.length) {
                            speed[i] = party[i].SPEED[party[i].LV];
                            order[i] = i;
                        }
                        else {
                            speed[i] = enemy[i-party.length].SPEED[enemy[i-party.length].LV];
                            order[i] = 10+(i-4);
                        }
                    }

                    //ソート(順番決め)
                    for (int i = 0; i < party.length+enemy.length; i++) {
                        int temp;
                        for (int j = party.length+enemy.length-1; j > i; j--) {
                            if (speed[j-1] < speed[j]) {
                                temp = speed[j-1];
                                speed[j-1] = speed[j];
                                speed[j] = temp;

                                temp = order[j-1];
                                order[j-1] = order[j];
                                order[j] = temp;
                            }
                        }
                    }


                    init = S_TURNCHANGE;
                    break;

                //誰のターンかを決める（バトル中）
                case S_TURNCHANGE :
                    battleturn %= party.length+enemy.length;
                    if (isinbattle) {
                        while (true) {
                            if (order[battleturn] < 10) {   //味方
                                partytarget = order[battleturn];
                                init = S_COMMAND;
                                if (party[partytarget].HP > 0) {
                                    isdefence[partytarget] = false;
                                    battleturn++;
                                    break;
                                }
                            }
                            else {
                                enemytarget = order[battleturn] - 10;
                                init = S_DEFENCE;
                                if (enemy[enemytarget].HP > 0) {
                                    while (true) {
                                        partytarget = rand(party.length);
                                        if (party[partytarget].HP > 0) break;
                                    }
                                    battleturn++;
                                    break;
                                }
                            }
                            battleturn++;
                        }
                    }
                    else {
                        init = S_MAP;
                    }
                    break;

                //command
                case S_COMMAND :
                    drawBattle("攻撃", "防御", "スキル", "逃げる", partytarget);
                    key = KEY_NONE;
                    while (init == -1) {
                        if (key == KEY_1) {
                            init = S_ATTACK;

                            //誰にする攻撃かを選ばせる
//                            enemytarget = -1;
//                            while (enemytarget == -1) {
//                                     if (key == KEY_MONSTER1 && enemy[0].HP > 0) choice = party[partytarget].SKILL[0];
//                                else if (key == KEY_MONSTER2 && enemy[1].HP > 0) choice = party[partytarget].SKILL[1];
//                                else if (key == KEY_MONSTER3 && enemy[2].HP > 0) choice = party[partytarget].SKILL[2];
//                                else if (key == KEY_MONSTER4 && enemy[3].HP > 0) choice = party[partytarget].SKILL[3];
//                                else if (key == KEY_REDO) {
//                                    init = S_COMMAND;          //SP不足はコマンドからやり直し
//                                    break;
//                                }
//                                sleep(100);
//                            }

                        }
                        else if (key == KEY_2) {
                            isdefence[partytarget] = true;
                            init = S_TURNCHANGE;
                        }
                        else if (key == KEY_3) {
                            isskill = true;
                            init = S_ATTACK;
                            //誰にする攻撃かを選ばせる

                        }
                        else if (key == KEY_4) {
                            init = S_ESCAPE;
                        }
                        sleep(100);
                    }
                    break;

                //attack process
                case S_ATTACK:
                    if (!isskill) {
                        drawBattle(party[partytarget].NAME + "の攻撃!");
                        waitSelect();
                        if (rand(100) <= 90) {
                            //flush
                            for (int i = 0; i < 10; i++) {
                                drawBattle(party[partytarget].NAME + "の攻撃!", i % 2 == 0);
                                sleep(100);
                            }

                            //attack calculation
                            int damage = party[partytarget].ATTACK[party[partytarget].LV] - enemy[enemytarget].DEFENCE[enemy[enemytarget].LV] + rand(10);
                            if (damage <= 1) damage = 1;

                            //会心の一撃
                            if (rand(100) <= 8) {
                                drawBattle("急所に当たった!");
                                waitSelect();
                                damage += enemy[enemytarget].DEFENCE[enemy[enemytarget].LV];
                                damage *= 2;
                            }

                            //message
                            drawBattle(damage + "ダメージを与えた");
                            waitSelect();

                            //calculate HP
                            enemy[enemytarget].HP -= damage;
                            if (enemy[enemytarget].HP <= 0) enemy[enemytarget].HP = 0;
                        } else {
                            drawBattle(enemy[enemytarget].NAME + "は回避した");
                            waitSelect();
                        }
                    }
                    //スキルフェーズ
                    else {
                        showSkill(Skill.NAME[party[partytarget].SKILL[0]], Skill.NAME[party[partytarget].SKILL[1]], Skill.NAME[party[partytarget].SKILL[2]], Skill.NAME[party[partytarget].SKILL[3]], partytarget);
                        waitSelect();
                        int choice = -1;
                        key = KEY_NONE;
                        scene = S_SKILL;
                        while (choice == -1) {
                                 if (key == KEY_1 && party[partytarget].SKILL[0] != 0) choice = party[partytarget].SKILL[0];
                            else if (key == KEY_2 && party[partytarget].SKILL[1] != 0) choice = party[partytarget].SKILL[1];
                            else if (key == KEY_3 && party[partytarget].SKILL[2] != 0) choice = party[partytarget].SKILL[2];
                            else if (key == KEY_4 && party[partytarget].SKILL[3] != 0) choice = party[partytarget].SKILL[3];
                            else if (key == KEY_REDO) {
                                     init = S_COMMAND;          //SP不足はコマンドからやり直し
                                     break;
                                 }
                            sleep(100);
                        }
                        if (key != KEY_REDO) {  //次のコマンド
                            scene = S_ATTACK;   //もとに戻す
                            Skill.Skillcast(this, choice, party[partytarget], enemy[enemytarget]);
                        }
                        isskill = false;
                    }

                    if (init != S_COMMAND) {    //戻るしてなかったら
                        init = S_TURNCHANGE;
                    }
                    //victory
                    boolean isvictory = true;
                    for (int i = 0; i < enemy.length; i++) if (enemy[i].HP != 0) isvictory = false;
                    if (isvictory) {
                        isinbattle = false; //バトル終了
                        //message
                        drawBattle("モンスターたちを倒した", false);
                        waitSelect();
                        int exp = 0;
                        for (int i = 0; i < enemy.length; i++) exp += enemy[i].DROPEXP[enemy[i].LV];
                        drawBattle(exp + " 経験値を手に入れた", false);
                        waitSelect();

                        for (int i = 0; i < party.length; i++) {
                            if (party[i].LV != party[i].MAXLV) {    //レベルマックスなら無視
                                //calculate EXP
                                party[i].GETEXP += exp;
                                while (party[i].EXP[party[i].LV] <= party[i].GETEXP && party[i].LV != party[i].MAXLV) {
                                    party[i].GETEXP -= party[i].EXP[party[i].LV];
                                    party[i].LV++;
                                    party[i].HP = party[i].MAXHP[party[i].LV] * party[i].HP / party[i].MAXHP[party[i].LV - 1];    //体力回復(レベルアップ前のHPゲージを保つ計算)
                                    party[i].SP = party[i].MAXSP[party[i].LV] * party[i].SP / party[i].MAXSP[party[i].LV - 1];    //SP回復
                                    drawBattle(party[i].NAME + "は", "LV " + party[i].LV + " にアップした", false);
                                    waitSelect();
                                }

//                            if (party[0].LV != party[0].MAXLV) {
//                                drawBattle("次のレベルアップまで  ", ("あと ") + (party[0].EXP[party[0].LV] - party[0].GETEXP) + (" 経験値"), false);
//                                waitSelect();
//                            }
                                if (party[i].LV == party[i].MAXLV) {
                                    drawBattle(party[i].NAME, "はレベル最大に到達した", false);
                                    waitSelect();
                                }
                            }
                        }

                        //ドロップマネー
                        int dropmoney = 0;
                        for (int i = 0; i < enemy.length; i++) dropmoney += enemy[i].DROPMAONEY[enemy[i].LV];
                        drawBattle(dropmoney + "G を手に入れた", false);
                        Money += dropmoney;
                        waitSelect();

                        init = S_MAP;   //マップに遷移
                    }
                    break;

                //防御
                case S_DEFENCE:
                    //message
                    if (enemy[enemytarget].ESCAPEPERCENT <= rand(100)) {
                        drawBattle("相手の" + enemy[enemytarget].NAME + "の攻撃");
                        waitSelect();
                        if (rand(100) <= 90) {
                            //flush
                            for (int i = 0; i < 10; i++) {
                                if (i % 2 == 0) {
                                    g.lock();
                                    g.setColor(Color.rgb(255, 255, 255));
                                    g.fillRect(0, 0, W, H);
                                    g.unlock();
                                } else {
                                    drawBattle(enemy[enemytarget].NAME + "の攻撃");
                                }
                                sleep(100);
                            }
                            //calculate for defence
                            int damage = enemy[enemytarget].ATTACK[enemy[enemytarget].LV] - party[partytarget].DEFENCE[party[partytarget].LV] + rand(10);
                            if (damage <= 1) damage = 1;

                            //会心の一撃
                            if (rand(100) <= 8) {
                                drawBattle("痛恨の一撃!");
                                waitSelect();
                                damage += party[partytarget].DEFENCE[party[partytarget].LV];
                                damage *= 2;
                            }

                            if (isdefence[partytarget]) {    //防御をしていたら
                                damage /= 3;
                            }

                            //message
                            drawBattle(party[partytarget].NAME + "は", damage + "ダメージを受けた", true);
                            waitSelect();

                            //calculate HP
                            party[partytarget].HP -= damage;
                            if (party[partytarget].HP <= 0) party[partytarget].HP = 0;
                        } else {
                            drawBattle(party[partytarget].NAME + "は回避した");
                            waitSelect();
                        }
                        init = S_TURNCHANGE;

                        //全滅
                        boolean isdestruction = true;
                        for (int i = 0; i < party.length; i++) if (party[i].HP != 0) isdestruction = false;
                        if (isdestruction) {
                            drawBattle("全滅してしまった");
                            waitSelect();
                            init = S_START;
                            isinbattle = false;
                        }
                    }
                    else {
                        drawBattle(enemy[enemytarget].NAME + "は逃げ出した", false);
                        waitSelect();
                        init = S_MAP;
                        isinbattle = false;
                    }
                    break;

               //escape
                case S_ESCAPE :
                    //message
                    drawBattle(party[0].NAME + "たちは逃げ出した");
                    waitSelect();

                    //calculation for escape
                    if (rand(100) <= 60) {
                        drawBattle(enemy[0].NAME+"たちは回りこんだ");
                        waitSelect();
                        init = S_DEFENCE;
                    }
                    else  {
                        init = S_TURNCHANGE;
                        isinbattle = false;
                    }
                    break;
            }

            //sleep
            key = KEY_NONE;
            sleep(200);
        }
    }


    public void drawBattle(String message) {
        boolean isdestruction = true;
        for (int i = 0; i < enemy.length; i++) if (enemy[i].HP != 0) isdestruction = false;
        drawBattle(message, !isdestruction);
    }


    public void drawBattle(String message, boolean visible) {
        boolean isdestruction = true;
        for (int i = 0; i < party.length; i++) if (party[i].HP != 0) isdestruction = false;
        int color = !isdestruction ? Color.rgb(0, 0, 0) : Color.rgb(255, 0, 0);
        g.lock();
        g.setColor(color);
        g.fillRect(0, 0, W, H);
        battleStatus();
        if (visible) {
            for (int i = 0; i < enemy.length; i++)
                if(enemy[i].HP > 0)
                switch (enemy.length) {
                case 1:
                        g.drawMonsterInBattle(bmpmonster[enemy[i].MONSTERNUMBER],
                                W/2 - (bmpmonster[enemy[i].MONSTERNUMBER].getWidth())/6,
                                H/2 - bmpmonster[enemy[i].MONSTERNUMBER].getHeight()/6 - 30);
                    break;
                case 2:
                        g.drawMonsterInBattle(bmpmonster[enemy[i].MONSTERNUMBER],
                                220 + (bmpmonster[enemy[i].MONSTERNUMBER].getWidth() * i),
                                H/2 - bmpmonster[enemy[i].MONSTERNUMBER].getHeight()/6 - 30);
                    break;
                case 3:
                        g.drawMonsterInBattle(bmpmonster[enemy[i].MONSTERNUMBER],
                                130 + ((bmpmonster[enemy[i].MONSTERNUMBER].getWidth()-30) * i),
                                H/2 - bmpmonster[enemy[i].MONSTERNUMBER].getHeight()/6 - 30);
                    break;
                case 4:
                        g.drawMonsterInBattle(bmpmonster[enemy[i].MONSTERNUMBER],
                                70 + ((bmpmonster[enemy[i].MONSTERNUMBER].getWidth()-60) * i),
                                H/2 - bmpmonster[enemy[i].MONSTERNUMBER].getHeight()/6 - 30);
                    break;
            }
        }
        g.setColor(Color.rgb(255, 255, 255));
        g.fillRect((W - 504) / 2, H - 122, 504, 104);
        g.setColor(color);
        g.fillRect((W - 500) / 2, H - 120, 500, 100);
        g.setColor(Color.rgb(255, 255, 255));
        g.setTextSize(32);

        g.drawText(message, (W - 500) / 2 + 50, 370 - (int) g.getFontMetrics().top);
        g.unlock();
    }

    public void drawBattle(String message1, String message2, boolean visible) {
        boolean isdestruction = true;
        for (int i = 0; i < party.length; i++) if (party[i].HP != 0) isdestruction = false;
        int color = !isdestruction ? Color.rgb(0, 0, 0) : Color.rgb(255, 0, 0);
        g.lock();
        g.setColor(color);
        g.fillRect(0, 0, W, H);
        battleStatus();
        if (visible) {
            for (int i = 0; i < enemy.length; i++)
                if (enemy[i].HP > 0)
                    switch (enemy.length) {
                        case 1:
                            g.drawMonsterInBattle(bmpmonster[enemy[i].MONSTERNUMBER],
                                    W / 2 - (bmpmonster[enemy[i].MONSTERNUMBER].getWidth()) / 6,
                                    H / 2 - bmpmonster[enemy[i].MONSTERNUMBER].getHeight() / 6 - 30);
                            break;
                        case 2:
                            g.drawMonsterInBattle(bmpmonster[enemy[i].MONSTERNUMBER],
                                    220 + (bmpmonster[enemy[i].MONSTERNUMBER].getWidth() * i),
                                    H / 2 - bmpmonster[enemy[i].MONSTERNUMBER].getHeight() / 6 - 30);
                            break;
                        case 3:
                            g.drawMonsterInBattle(bmpmonster[enemy[i].MONSTERNUMBER],
                                    130 + ((bmpmonster[enemy[i].MONSTERNUMBER].getWidth() - 30) * i),
                                    H / 2 - bmpmonster[enemy[i].MONSTERNUMBER].getHeight() / 6 - 30);
                            break;
                        case 4:
                            g.drawMonsterInBattle(bmpmonster[enemy[i].MONSTERNUMBER],
                                    70 + ((bmpmonster[enemy[i].MONSTERNUMBER].getWidth() - 60) * i),
                                    H / 2 - bmpmonster[enemy[i].MONSTERNUMBER].getHeight() / 6 - 30);
                            break;
                    }
        }
        g.setColor(Color.rgb(255, 255, 255));
        g.fillRect((W - 504) / 2, H - 122, 504, 104);
        g.setColor(color);
        g.fillRect((W - 500) / 2, H - 120, 500, 100);
        g.setColor(Color.rgb(255, 255, 255));
        g.setTextSize(32);

        g.drawText(message1, (W - 500) / 2 + 50, 370 - (int) g.getFontMetrics().top);
        g.drawText(message2, (W - 500) / 2 + 50, 370 - (int) g.getFontMetrics().top*2 +10);
        g.unlock();
    }

    public void drawBattle (String message1, String message2, String message3, String message4, int target) {
        boolean isdestruction = true;
        for (int i = 0; i < party.length; i++) if (party[i].HP != 0) isdestruction = false;
        int color = !isdestruction ? Color.rgb(0, 0, 0) : Color.rgb(255, 0, 0);
        g.lock();
        g.setColor(color);
        g.fillRect(0, 0, W, H);
        battleStatus(target);
        for (int i = 0; i < enemy.length; i++) {
            if (enemy[i].HP > 0)
                switch (enemy.length) {
                    case 1:
                        g.drawMonsterInBattle(bmpmonster[enemy[i].MONSTERNUMBER],
                                W / 2 - (bmpmonster[enemy[i].MONSTERNUMBER].getWidth()) / 6,
                                H / 2 - bmpmonster[enemy[i].MONSTERNUMBER].getHeight() / 6 - 30);
                        break;
                    case 2:
                        g.drawMonsterInBattle(bmpmonster[enemy[i].MONSTERNUMBER],
                                220 + (bmpmonster[enemy[i].MONSTERNUMBER].getWidth() * i),
                                H / 2 - bmpmonster[enemy[i].MONSTERNUMBER].getHeight() / 6 - 30);
                        break;
                    case 3:
                        g.drawMonsterInBattle(bmpmonster[enemy[i].MONSTERNUMBER],
                                130 + ((bmpmonster[enemy[i].MONSTERNUMBER].getWidth() - 30) * i),
                                H / 2 - bmpmonster[enemy[i].MONSTERNUMBER].getHeight() / 6 - 30);
                        break;
                    case 4:
                        g.drawMonsterInBattle(bmpmonster[enemy[i].MONSTERNUMBER],
                                70 + ((bmpmonster[enemy[i].MONSTERNUMBER].getWidth() - 60) * i),
                                H / 2 - bmpmonster[enemy[i].MONSTERNUMBER].getHeight() / 6 - 30);
                        break;
                }
        }

        g.setColor(Color.rgb(255, 255, 255));
        g.fillRect((W - 504) / 2, H - 122, 504, 104);
        g.setColor(color);
        g.fillRect((W - 500) / 2, H - 120, 500, 100);
        g.setColor(Color.rgb(255, 255, 255));
        g.setTextSize(26);

        g.drawText(message1, (W - 500) / 2 + 50, 370 - (int) g.getFontMetrics().top);
        g.drawText(message2, (W - 500) / 2 + 270, 370 - (int) g.getFontMetrics().top);
        g.drawText(message3, (W - 500) / 2 + 50, 370 - (int) g.getFontMetrics().top * 2 + 10);
        g.drawText(message4, (W - 500) / 2 + 270, 370 - (int) g.getFontMetrics().top*2 +10);
        g.unlock();
    }

    public void showSkill (String message1, String message2, String message3, String message4, int target) {
        boolean isdestruction = true;
        for (int i = 0; i < party.length; i++) if (party[i].HP != 0) isdestruction = false;
        int color = !isdestruction ? Color.rgb(0, 0, 0) : Color.rgb(255, 0, 0);
        g.lock();
        g.setColor(color);
        g.fillRect(0, 0, W, H);
        battleStatus(target);
        for (int i = 0; i < enemy.length; i++)
            if (enemy[i].HP > 0)
                switch (enemy.length) {
                    case 1:
                        g.drawMonsterInBattle(bmpmonster[enemy[i].MONSTERNUMBER],
                                W / 2 - (bmpmonster[enemy[i].MONSTERNUMBER].getWidth()) / 6,
                                H / 2 - bmpmonster[enemy[i].MONSTERNUMBER].getHeight() / 6 - 30);
                        break;
                    case 2:
                        g.drawMonsterInBattle(bmpmonster[enemy[i].MONSTERNUMBER],
                                220 + (bmpmonster[enemy[i].MONSTERNUMBER].getWidth() * i),
                                H / 2 - bmpmonster[enemy[i].MONSTERNUMBER].getHeight() / 6 - 30);
                        break;
                    case 3:
                        g.drawMonsterInBattle(bmpmonster[enemy[i].MONSTERNUMBER],
                                130 + ((bmpmonster[enemy[i].MONSTERNUMBER].getWidth() - 30) * i),
                                H / 2 - bmpmonster[enemy[i].MONSTERNUMBER].getHeight() / 6 - 30);
                        break;
                    case 4:
                        g.drawMonsterInBattle(bmpmonster[enemy[i].MONSTERNUMBER],
                                70 + ((bmpmonster[enemy[i].MONSTERNUMBER].getWidth() - 60) * i),
                                H / 2 - bmpmonster[enemy[i].MONSTERNUMBER].getHeight() / 6 - 30);
                        break;
                }

        g.setColor(Color.rgb(255, 255, 255));
        g.fillRect((W - 504) / 2, H - 122, 504, 104);
        g.setColor(color);
        g.fillRect((W - 500) / 2, H - 120, 500, 100);
        g.setColor(Color.rgb(255, 255, 255));
        g.setTextSize(26);

        g.drawText(message1, (W - 500) / 2 + 50, 370 - (int) g.getFontMetrics().top);
        g.drawText(message2, (W - 500) / 2 + 240, 370 - (int) g.getFontMetrics().top);
        g.drawText(message3, (W - 500) / 2 + 50, 370 - (int) g.getFontMetrics().top* 2 + 10);
        g.drawText(message4, (W - 500) / 2 + 240, 370 - (int) g.getFontMetrics().top*2 +10);

        g.setTextSize(20);
        g.drawText("戻る", W/2+200, 370 - (int) g.getFontMetrics().top*2 +30);

        g.unlock();
    }


    public void mapStatus() {
        g.setTextSize(18);
        if (!isstatushide) {
            for (int i = 0; i < party.length; i++) {
                int color = (party[i].HP != 0) ? Color.rgb(157, 204, 224) : Color.rgb(255, 0, 0);    //水色or赤
                g.setColor(color);
                g.fillRect(W - 195, 60 + 90 * i, 185, 85);     //味方のステータス表示
                g.setColor(Color.rgb(0, 0, 0));   //黒
                g.drawText(party[i].NAME + " Lv." + party[i].LV, W - 185, 85 + 90 * i);
                g.drawText("HP  " + party[i].HP + "/" + party[i].MAXHP[party[i].LV], W - 180, 85 + 90 * i - (int) g.getFontMetrics().top + 5);
                g.drawText("SP  " + party[i].SP + "/" + party[i].MAXSP[party[i].LV], W - 180, 85 + 90 * i - (int) g.getFontMetrics().top * 2 + 8);
            }
            //tool to hide status
            g.setColor(Color.rgb(231, 232, 226));
            g.fillRect(W - 130, H - 50, 120, 40);
            g.setColor(Color.rgb(0, 0, 0));
            g.drawText("非表示", W - 100, H - 25);
        }
        else {
            g.setColor(Color.rgb(231, 232, 226));
            g.fillRect(W - 130, H - 50, 120, 40);
            g.setColor(Color.rgb(0, 0, 0));
            g.drawText("   表示", W - 100, H - 25);
        }
        g.setColor(Color.rgb(231, 232, 226));
        g.fillRect(W - 130, 10, 120, 40);   //金表示
        g.setColor(Color.rgb(0, 0, 0));
        g.drawText(Money + "G", W - 130 + 10, 15 - (int) g.getFontMetrics().top);
    }

    public void battleStatus() {
        for (int i = 0; i < party.length; i++) {
            int color = (party[i].HP != 0) ? Color.rgb(231, 232, 226) : Color.rgb(255, 0, 0);
            g.setColor(color);
            g.fillRect(20+186*i, 15, 185, 80);     //味方のステータス表示

            g.setColor(Color.rgb(0, 0, 0));   //黒
            g.setTextSize(18);
            g.drawText(party[i].NAME + " Lv." + party[i].LV, 30+186*i, 20 - (int) g.getFontMetrics().top);
            g.drawText("HP  " + party[i].HP + "/" + party[i].MAXHP[party[i].LV], 30+186*i, 20 - (int) g.getFontMetrics().top * 2 + 5);
            g.drawText("SP  " + party[i].SP + "/" + party[i].MAXSP[party[i].LV], 30+186*i, 20 - (int) g.getFontMetrics().top * 3 + 8);
        }
    }

    public void battleStatus(int target) {
        for (int i = 0; i < party.length; i++) {
            int color;
            if (i!=target) {
                color = (party[i].HP != 0) ? Color.rgb(231, 232, 226) : Color.rgb(255, 0, 0);
            }
            else {
                color = (party[i].HP != 0) ? Color.rgb(255, 255, 0) : Color.rgb(255, 0, 0);
            }
            g.setColor(color);
            g.fillRect(20+186*i, 15, 185, 80);     //味方のステータス表示

            g.setColor(Color.rgb(0, 0, 0));   //黒
            g.setTextSize(18);
            g.drawText(party[i].NAME + " Lv." + party[i].LV, 30+186*i, 20 - (int) g.getFontMetrics().top);
            g.drawText("HP  " + party[i].HP + "/" + party[i].MAXHP[party[i].LV], 30+186*i, 20 - (int) g.getFontMetrics().top * 2 + 5);
            g.drawText("SP  " + party[i].SP + "/" + party[i].MAXSP[party[i].LV], 30+186*i, 20 - (int) g.getFontMetrics().top * 3 + 8);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX = (int)(event.getX()*W/getWidth());
        int touchY = (int)(event.getY()*H/getHeight());
        int touchAction = event.getAction();
        if(touchAction ==MotionEvent.ACTION_DOWN) {
            if (scene == S_MAP) {
                //十字キー
                if (W/2-40+80*(-4) < touchX && touchX < W/2-40+80*(-2) && H/2-40+80 < touchY && touchY < H/2-40+80*3) {
                    if (Math.abs(touchX - (W / 2 - 40 + 80 * (-3))) > Math.abs(touchY - (H / 2 - 40 + 80 * 2))) {
                        key = (touchX - (W / 2 - 40 + 80 * (-3)) < 0) ? KEY_LEFT : KEY_RIGHT;
                    } else {
                        key = (touchY - (H / 2 - 40 + 80 * 2) < 0) ? KEY_UP : KEY_DOWN;
                    }
                }
                //ステータス表示・非表示
                if (W-130 < touchX && touchX < W - 10 && H - 50 < touchY && touchY < H - 10) {
                    isstatushide = !isstatushide;
                }
            }
            else if (scene == S_APPEAR ||  scene == S_ATTACK || scene == S_DEFENCE || scene == S_ESCAPE) {
                key = KEY_SELECT;
            }
            else if (scene == S_COMMAND) {
                if (W/2-250 < touchX && touchX < W/2 && H-190 < touchY && touchY < H-70) {
                    key = KEY_1;
                }
                else if (W/2 < touchX && touchX < W/2+250 && H-190 < touchY && touchY < H-70) {
                    key = KEY_2;
                }
                else if (W/2-250 < touchX && touchX < W/2 && H-70 < touchY && touchY < H) {
                    key = KEY_3;
                }
                else if (W/2 < touchX && touchX < W/2+250 && H-70 < touchY && touchY < H) {
                    key = KEY_4;
                }
            }
            else if (scene == S_SKILL) {
                if (W/2-250 < touchX && touchX < W/2-25 && H-190 < touchY && touchY < H-70) {
                    key = KEY_1;
                }
                else if (W/2-25 < touchX && touchX < W/2+200 && H-190 < touchY && touchY < H-70) {
                    key = KEY_2;
                }
                else if (W/2-250 < touchX && touchX < W/2-25 && H-70 < touchY && touchY < H-10) {
                    key = KEY_3;
                }
                else if (W/2-25 < touchX && touchX < W/2+200 && H-70 < touchY && touchY < H-10) {
                    key = KEY_4;
                }
                else if (W/2+200 < touchX && touchX < W/2+250 && H-50 < touchY && touchY < H-10) {
                    key = KEY_REDO;
                }
            }
        }
        return true;
    }


    //wait fot key
    public void waitSelect() {
        key = KEY_NONE;
        while (key!=KEY_SELECT) sleep(100);
    }


    public void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
        }
    }


    //generates Random number
    private static Random rand = new Random();
    public static int rand(int num) {
        return (rand.nextInt(num));
    }

    private static Bitmap readBitmap(Context context, String name) {
        int resID = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        return BitmapFactory.decodeResource(context.getResources(), resID);
    }
}
