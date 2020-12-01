package com.topmngr.game.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;


public class Assets implements AssetErrorListener,Disposable {
    public static final Assets instance = new Assets();
    private AssetManager assetManager;

    public Preferences
            settingsPref,
            playerDataPref;

    public BitmapFont mainFont, mainFontSmall;
    final String FONT_CHARS = "абвгдежзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyzАБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";
    public Label.LabelStyle mainStyle;
    public Skin skin;
    public Window.WindowStyle mainWindowStyle;
    public TextButton.TextButtonStyle mainButtonStyle;
    public TextField.TextFieldStyle mainTextFieldStyle;

    public TextureRegion
            topBarLine,
            btnSummary[] = new TextureRegion[3],
            btnStorage[] = new TextureRegion[3],
            btnBank[] = new TextureRegion[3],
            btnIndustry[] = new TextureRegion[3],
            btnProd[] = new TextureRegion[3],
            btnManage[] = new TextureRegion[3],
            bgloading,
            bglock;

    public NinePatch
            bgKontent,
            bgTextFieldChat,
            bgButton,
            bgleftBar,
            bgRatePlayers,
            bgTopbar,
            bgTopbarLabel,
            bgWindow,
            bgDiscover,
            bgMainMenu;

    public String rulesText;

    public enum Sounds{
        CLICK, ALERT
    }

    public HashMap<Sounds,Sound> sounds;

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        Gdx.app.error("Assets", "Could not load asset '"+asset.fileName+"'",(Exception)throwable);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }

    public static class PlayerData {
        private static String name;
        private static int
                        gamesAmount,
                        winAmount,
                        bankruptAmount,
                        biggestRIF,
                        biggestAccumulatedNetProfit,
                        exitedGamesAmount;

        static void init() {
            setName(Assets.instance.playerDataPref.getString("pName", "Player"));
            setGamesAmount(Assets.instance.playerDataPref.getInteger("pGamesAmount", 0));
            setWinAmount(Assets.instance.playerDataPref.getInteger("pWinAmount", 0));
            setBankruptAmount(Assets.instance.playerDataPref.getInteger("pBankruptAmount", 0));
            setBiggestRIF(Assets.instance.playerDataPref.getInteger("pBiggestRIF", 0));
            setBiggestAccumulatedNetProfit(Assets.instance.playerDataPref.getInteger("pAllAccumulatedNetProfit", 0));
            setExitedGamesAmount(Assets.instance.playerDataPref.getInteger("pExitedGamesAmount", 0));
        }
        public static String getStats() {
            String s;
            s = "[cDarkBlue]Кол-во сыгранных игр: \t\t[cPureWhite]"+getGamesAmount() + "\n";
            s += "[cDarkBlue]Кол-во покинутых игр: \t\t[cPureWhite]"+getExitedGamesAmount() + "\n";
            s += "[cDarkBlue]Кол-во побед: \t\t[cPureWhite]"+getWinAmount() + "\n";
            s += "[cDarkBlue]Кол-во банкротств: \t\t[cPureWhite]"+getBankruptAmount() + "\n";
            s += "[cDarkBlue]Макс. РИФ: \t\t[cPureWhite]"+getBiggestRIF() + "\n";
            s += "[cDarkBlue]Макс. нак. приб.: \t\t[cPureWhite]"+ getBiggestAccumulatedNetProfit() + " тыс.";

            return s;
        }

        public static String getName() {
            return PlayerData.name;
        }
        public static void setName(String name) {
            PlayerData.name = name;
            Assets.instance.playerDataPref.putString("pName",name);
            Assets.instance.playerDataPref.flush();
        }

        public static int getGamesAmount() {
            return PlayerData.gamesAmount;
        }
        public static void setGamesAmount(int amount) {
            PlayerData.gamesAmount = amount;
            Assets.instance.playerDataPref.putInteger("pGamesAmount",amount);
            Assets.instance.playerDataPref.flush();
        }
        public static void addGamesAmount() {
            PlayerData.gamesAmount ++;
            Assets.instance.playerDataPref.putInteger("pGamesAmount",PlayerData.gamesAmount);
            Assets.instance.playerDataPref.flush();
        }

        public static int getWinAmount() {
            return PlayerData.winAmount;
        }
        public static void setWinAmount(int amount) {
            PlayerData.winAmount = amount;
            Assets.instance.playerDataPref.putInteger("pWinAmount",amount);
            Assets.instance.playerDataPref.flush();
        }
        public static void addWinAmount() {
            PlayerData.winAmount ++;
            Assets.instance.playerDataPref.putInteger("pWinAmount",PlayerData.winAmount);
            Assets.instance.playerDataPref.flush();
        }

        public static int getBankruptAmount() {
            return PlayerData.bankruptAmount;
        }
        public static void setBankruptAmount(int amount) {
            PlayerData.bankruptAmount = amount;
            Assets.instance.playerDataPref.putInteger("pBankruptAmount",amount);
            Assets.instance.playerDataPref.flush();
        }
        public static void addBankruptAmount() {
            PlayerData.bankruptAmount ++;
            Assets.instance.playerDataPref.putInteger("pBankruptAmount",PlayerData.bankruptAmount);
            Assets.instance.playerDataPref.flush();
        }

        public static int getBiggestRIF() {
            return PlayerData.biggestRIF;
        }
        public static void setBiggestRIF(int amount) {
            PlayerData.biggestRIF = amount;
            Assets.instance.playerDataPref.putInteger("pBiggestRIF",amount);
            Assets.instance.playerDataPref.flush();
        }

        public static int getBiggestAccumulatedNetProfit() {
            return PlayerData.biggestAccumulatedNetProfit;
        }
        public static void setBiggestAccumulatedNetProfit(int amount) {
            PlayerData.biggestAccumulatedNetProfit = amount;
            Assets.instance.playerDataPref.putInteger("pAllAccumulatedNetProfit",amount);
            Assets.instance.playerDataPref.flush();
        }

        public static int getExitedGamesAmount() {
            return PlayerData.exitedGamesAmount;
        }
        public static void setExitedGamesAmount(int amount) {
            PlayerData.exitedGamesAmount = amount;
            Assets.instance.playerDataPref.putInteger("pExitedGamesAmount",amount);
            Assets.instance.playerDataPref.flush();
        }
        public static void addExitedGamesAmount() {
            PlayerData.exitedGamesAmount ++;
            Assets.instance.playerDataPref.putInteger("pExitedGamesAmount",PlayerData.exitedGamesAmount);
            Assets.instance.playerDataPref.flush();
        }
    }

    public boolean isDeveloper = false;

    public final String[] colors = {
            "[cGreen]",
            "[cRed]",
            "[cBlue]",
            "[cYellow]",
            "[cOrange]",
            "[cPurple]",
            "[cLightBlue]",
            "[cBrown]",
            "[cGray]"
    };
    private Assets(){}

    public void  load (AssetManager assetManager) {
        this.assetManager = assetManager;
        Texture.setAssetManager(assetManager);
        assetManager.setErrorListener(this);
        sounds = new HashMap<Sounds, Sound>();

        assetManager.load("skins/slider.atlas", TextureAtlas.class);
        assetManager.load("skins/slider.json", Skin.class, new SkinLoader.SkinParameter("skins/slider.atlas"));
        assetManager.load("mainUI.pack", TextureAtlas.class);
        assetManager.load("sounds/click.mp3",Sound.class);
        assetManager.load("sounds/alert.mp3",Sound.class);
        assetManager.finishLoading();

        playerDataPref = Gdx.app.getPreferences("playerDataPref");
        PlayerData.init();

        genFont();
        mainFont.getData().markupEnabled = true;
        mainFontSmall.getData().markupEnabled = true;
        mainStyle = new Label.LabelStyle(mainFont, Color.WHITE);


        topBarLine = new TextureRegion(new Texture("barTopLine.png"));
        Colors.put("cGreen",Color.valueOf("1d821d"));
        Colors.put("cRed",Color.valueOf("821d1d"));
        Colors.put("cBlue",Color.valueOf("1d3e82"));
        Colors.put("cYellow",Color.valueOf("c3b117"));
        Colors.put("cOrange",Color.valueOf("c37017"));
        Colors.put("cPurple",Color.valueOf("6633FF"));
        Colors.put("cLightBlue",Color.valueOf("00CCFF"));
        Colors.put("cBrown",Color.valueOf("993300"));
        Colors.put("cGray",Color.valueOf("666666"));
        Colors.put("cDarkBlue",Color.valueOf("2c4c56"));
        Colors.put("cPureWhite",Color.valueOf("ebf7ff"));

        skin = assetManager.get("skins/slider.json",Skin.class);
        skin.getFont("default-font").getData().markupEnabled = true;
        TextureAtlas atlas = assetManager.get("mainUI.pack", TextureAtlas.class);

        btnSummary[0] = atlas.findRegion("btn_sum2");
        btnSummary[1] = atlas.findRegion("btn_sum2_down");
        btnSummary[2] = atlas.findRegion("btn_sum2_sel");

        btnStorage[0] = atlas.findRegion("btn_storage2");
        btnStorage[1] = atlas.findRegion("btn_storage2_down");
        btnStorage[2] = atlas.findRegion("btn_storage2_sel");

        btnBank[0] = atlas.findRegion("btn_bank2");
        btnBank[1] = atlas.findRegion("btn_bank2_down");
        btnBank[2] = atlas.findRegion("btn_bank2_sel");

        btnIndustry[0] = atlas.findRegion("btn_ind2");
        btnIndustry[1] = atlas.findRegion("btn_ind2_down");
        btnIndustry[2] = atlas.findRegion("btn_ind2_sel");

        btnProd[0] = atlas.findRegion("btn_prod2");
        btnProd[1] = atlas.findRegion("btn_prod2_down");
        btnProd[2] = atlas.findRegion("btn_prod2_sel");

        btnManage[0] = atlas.findRegion("btn_manage2");
        btnManage[1] = atlas.findRegion("btn_manage2_down");
        btnManage[2] = atlas.findRegion("btn_manage2_sel");

        bgloading = atlas.findRegion("anim1");
        bglock = atlas.findRegion("closeIcon");

        
        bgKontent = atlas.createPatch("res_bg_main");
        bgTextFieldChat = atlas.createPatch("res_chat_msg_bg");
        bgButton = atlas.createPatch("res_dyn_btn");
        bgleftBar = atlas.createPatch("res_left_bg");
        bgRatePlayers = atlas.createPatch("res_rate_players_bg");
        bgTopbar = atlas.createPatch("res_top_bar_bg");
        bgTopbarLabel = atlas.createPatch("res_bg_topbar_label");
        bgWindow = atlas.createPatch("res_bg_window");
        bgDiscover = atlas.createPatch("bgDiscover");
        bgMainMenu = atlas.createPatch("bgMainMenu");

        //
        sounds.put(Sounds.CLICK,assetManager.get("sounds/click.mp3",Sound.class));
        sounds.put(Sounds.ALERT,assetManager.get("sounds/alert.mp3",Sound.class));//Gdx.audio.newSound(Gdx.files.internal("alert.wav"))


        mainWindowStyle = new Window.WindowStyle();
        mainWindowStyle.titleFontColor = Colors.get("PureWhite");
        mainWindowStyle.background = new Image(Assets.instance.bgWindow).getDrawable();
        mainWindowStyle.titleFont = mainFont;

        Drawable img = new Image(Assets.instance.bgButton).getDrawable();
        Drawable img2 = new Image(atlas.createPatch("res_dyn_btn_down")).getDrawable();
        mainButtonStyle = new TextButton.TextButtonStyle(img,img2,img,mainFontSmall);

        mainTextFieldStyle = new TextField.TextFieldStyle(mainFontSmall,Color.WHITE,
                skin.getDrawable("cursor"),skin.getDrawable("selection"),new Image(Assets.instance.bgTextFieldChat).getDrawable());

        rulesText =
                "Предположим что Вы являетесь менеджером фирмы. Ваша фирма производит мэкометр - товар, который производят только несколько фирм. Свой товар Вы продаёте на рынке, где у вас имеются конкуренты - другие фирмы. В начале игры ни одна из фирм (в игре может принять участие от 2 до 8 фирм) не имеет каких-либо преимуществ. Задача вашей фирмы - получить по окончании игры наибольший РИФ (Рейтинговый Индекс Фирмы). \n" +
                "РИФ зависит от следующих показателей: \n" +
                "- накопленная прибыль; \n" +
                "- потенциал спроса; \n" +
                "- потенциал предложения; \n" +
                "- эффективность загрузки фабрики; \n" +
                "- доля рынка; \n" +
                "- рост доли рынка. \n" +
                "Главная составляющая РИФа - Накопленная прибыль. \n" +
                "Вам предстоит: \n" +
                "установить Цену на Ваши мэкометры; \n" +
                "определиться в Производстве Вашей фабрики; \n" +
                "сделать вложения в Маркетинг (Маркетинг - это обширная деятельность в сфере рынка товаров и услуг, осуществляемая в целях стимулирования сбыта товаров. Большой составной частью маркетинга является реклама); \n" +
                "произвести амортизационные отчисления, а также сделать дополнительные капитальные вложения. Сумма этих двух позиций составляет Инвестиции. \n" +
                "определиться с затратами на НИОКР (научно-исследовательские опытно-конструкторские разработки). \n" +
                "\n" +
                "Цена: \n" +
                "Наверное, обьяснять смысл слова \"цена\" необходимости нет. Понятно что это сумма, за которую вы готовы продать единицу своего товара, а покупатели, соответственно, соглсаны его произвести. \n" +
                "Умение выбирать цену приходит только с опытом. Хотя, конечно, есть определенные предпосылки, из которых необходимо исходить при ее выборе. Во-первых, цена должна быть обязательно такой, чтобы при удачном стичени обстоятельств принести прибыль или хотя бы окупить все издержки текущего периода( в оббщем случае не желательно устанавливать цену ниже себестоимости) \n" +
                "Так же существует нижний и верхний предел цены: 15$ и 200$ соответсвенно. \n" +
                "\n" +
                "Производство: \n" +
                "Работать с этим параметром намного проще, чем с ценой. В игре есть вполне конкретное значение оптимальной загрузки мощности: Мощность следующего периода*0.8. В случае отклонения от этого значения возрастает себестоимость( при производстве в 100% происходит перегрузка станков, а при производстве меньшем 80% часть рабочих будеть бездельничать и всеравно получать вознаграждение, снизится эффективность труда, а себестоимость вырастет) \n" +
                "\n" +
                "\n" +
                "Маркетинг: \n" +
                "Маркетинг - важный инструмент, позволяющий вашей компании привлекать покупателей. Существует ряд особенностей: \n" +
                "1) Маркетинг действует только 1 период \n" +
                "2) Направляя свободные деньги в маркетинг, вы увеличиваете число заказов не только у себя, но и у конкурентов.Представте, что по телевизору рекламируют витамины \"Центрум\", и при этом утверждается, что в условиях весеннего авитаминоза и ослабления иммунитета, вашему организму жизненно необходимы витамины. В такой ситуации внимание людей будет сосредотачиваться не только на компании \"Центрум\", но и на витаминах как таковых, в результате чего возрастут продажи и других производителей витаминов, хотя они и не рекламировали продукцию столь активно. \n" +
                "3) Вы будете в приличном выигрыше, если только вы вложите приличную сумму денег. Однако если много фирм вложат большую сумму в маркетинг, то выиграет тот, кто ничего не вкладывал. \n" +
                "\n" +
                "Инвестиции: \n" +
                "Инвестици - это средства, которые вы тратите на поддержание своего оборудывания в рабочем состоянии, а также на увеличение мощности. \n" +
                "Инвестиции= Амортизация+ Доп.вложения \n" +
                "Амортизация=Капвложения*Норма амортизации(если проще, то мощность следующего периода*2) \n" +
                "Если не вложить деньги в амортизацию, вы потеряете часть станков и соотвественно сможете производить меньшее число продукции. \n" +
                "\n" +
                "НИОКР: \n" +
                "Вложения в НИОКР также имеют ряд особенностей: \n" +
                "1) Как и в случае с маркетингом, имеет место эффект нестандартности и оригинальности. Большие вложения в НИОКР привлекают покупателей не только к вам, но и к остальным участникам. \n" +
                "2) Главная особенность вложений в НИОКР - они действуют не только в том периоде, в котором были осуществленны, но и все оставшиеся периоды";
    }

    public void unload(String name){
        assetManager.unload(name);
    }

    public void load(String name,Class type){
        assetManager.load(name,type);
    }

    public void load(String name, Class type,AssetLoaderParameters parameters){
        assetManager.load(name,type,parameters);
    }

    public synchronized <T> T get(String fileName) {
        return assetManager.get(fileName);
    }

    public synchronized <T> T get(String fileName, Class<T> type) {
        return assetManager.get(fileName, type);
    }

    public synchronized <T> Array<T>  getAll(Class<T> type, Array<T> out) {
        return assetManager.getAll(type,out);
    }

    public boolean isLoaded(String name, Class classt) {
        return assetManager.isLoaded(name, classt);
    }

    public void finishLoading(){
        assetManager.finishLoading();
    }

    public boolean update(){
        return assetManager.update();
    }

    private void genFont() {
        final String FONT_PATH = "days.otf";


        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_PATH));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters = FONT_CHARS;
        parameter.size = 24;
        parameter.color = Color.WHITE;
        mainFont = generator.generateFont(parameter);


        parameter.characters = FONT_CHARS;
        parameter.size = 15;
        parameter.color = Color.WHITE;
        mainFontSmall = generator.generateFont(parameter);

        generator.dispose();
    }

    public Texture loadTexture (String file) {
        return new Texture(Gdx.files.internal(file));
    }

    public String getError() {
        FileHandle file = Gdx.files.local("errors.txt");
        return file.readString();
        //return errorPref.getString("CriticalError", "No errors!");
    }

    public void cleanErrors() {
        FileHandle file = Gdx.files.local("errors.txt");
        file.writeString("",false);
    }
}
