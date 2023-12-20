package com.mygdx.spidar;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import java.util.Arrays;

import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class Boot extends ApplicationAdapter {
	Pixmap pm;
	private Bullet bullet[]=new Bullet[100];
	private Enemy enemy[]=new Enemy[100];
	private Texture bulletImg;
	private Texture playerImage;
	private Texture enemyImage;
	private Texture redImg;
	private Texture greenImg;
	private Music backgroundMusic;
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Rectangle player;
	private Rectangle green;
	private boolean jump;
	private boolean fall;
	private double jumpvel;
	private Texture homeT;
	private Texture deadscreen;
	private Texture off;
	private Texture on;
	private Texture select;
	private Texture mouth;
	private Texture background;
	boolean home=true;
	boolean game=false;
	boolean died=false;

	int frame=0;
	int framecounter=0;
	int dir=0;
	int life;
	int timer =50;
	int mx;
	int my;
	int bulletcounter;
	int shootingtimer=100;
	int numberenemy=0;
	float allenemyvel=3;

	int screenheight;
	int screenwidth;

	int onx=0;
	int offx=-1000;
	int sy=0;
	int selected=0;
	boolean musicplaying=true;
	boolean musicalreadyplaying=false;
	int round=2;
	boolean shooting=false;
	float height;
	float width;
	@Override
	public void create() {
		height=Gdx.graphics.getHeight();
		width=Gdx.graphics.getWidth();
		allenemyvel=3;

		round=2;
		// load the images for the droplet and the bucket, 64x64 pixels each
		playerImage = new Texture(Gdx.files.internal("player0.png"));
		enemyImage=new Texture(Gdx.files.internal("enemy.png"));
		redImg = new Texture(Gdx.files.internal("red.png"));
		greenImg=new Texture(Gdx.files.internal("greenn.png"));
		bulletImg=new Texture(Gdx.files.internal("bullet.png"));
		homeT=new Texture(Gdx.files.internal("home.png"));
		on=new Texture(Gdx.files.internal("on.png"));
		off=new Texture(Gdx.files.internal("off.png"));
		select=new Texture(Gdx.files.internal("select.png"));
		deadscreen=new Texture(Gdx.files.internal("deadscreen.png"));
		mouth=new Texture(Gdx.files.internal("mouth.png"));
		background=new Texture(Gdx.files.internal("background.png"));

		for(int i=0;i<enemy.length;i++){
			enemy[i]=null;
		}

		life=100;

		pm = new Pixmap(Gdx.files.internal("cursorImage.png"));
		// load the drop sound effect and the rain background "music"


		if(musicplaying==true&&musicalreadyplaying==false) {
			backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));

			// start the playback of the background music immediately
			backgroundMusic.setLooping(true);
			backgroundMusic.play();
			musicalreadyplaying=true;
		}

		Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
		pm.dispose();

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();

		// create a Rectangle to logically represent the bucket
		player = new Rectangle();
		green=new Rectangle();
		player.x = Gdx.graphics.getWidth() / 2;
		player.y = Gdx.graphics.getHeight()/2;
		player.width=Gdx.graphics.getWidth()/15;
		player.height=Gdx.graphics.getWidth()/15;
		green.width=100;
		green.height=10;
		bulletcounter=0;
		jumpvel=Gdx.graphics.getWidth()/192;
		for(int i=0;i<10;i++){
			spawnEnemy();
			numberenemy++;
		}

	}

	@Override
	public void render() {
		// clear the screen with a dark blue color. The
		// arguments to clear are the red, green
		// blue and alpha component in the range [0,1]
		// of the color to be used to clear the screen.
		Gdx.gl.glClearColor( 0, 0, 0, 1 );
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );

		// tell the camera to update its matrices.
		camera.position.set(player.x,player.y,0);
		camera.update();

		screenwidth=(int)(player.x-Gdx.graphics.getWidth()/2)+Gdx.graphics.getWidth();
		screenheight=(int)(player.y-Gdx.graphics.getHeight()/2)+Gdx.graphics.getHeight();

		mx=(int)(player.x-Gdx.graphics.getWidth()/2)+Gdx.input.getX(0)+16;
		my=(int)(player.y-Gdx.graphics.getHeight()/2)+Gdx.graphics.getHeight()-Gdx.input.getY(0)-16;

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		batch.setProjectionMatrix(camera.combined);

		// begin a new batch and draw the player ;)


		if (home==true){
			batch.begin();
			batch.draw(homeT, 0, 0,width,height);
			batch.draw(on,onx,0,width,height);
			batch.draw(off,offx,0,width,height);
			batch.draw(select,0,sy,width,height);
			batch.end();
			selected=setSelect();
			homeHandleInput();
		} else if (game==true){
			batch.begin();
			batch.draw(background, -Gdx.graphics.getWidth()/2,-Gdx.graphics.getWidth()/2);
			for(int i=0;i<50;i++){
				if(enemy[i]!=null) {
					batch.draw(enemyImage, enemy[i].x, enemy[i].y,enemy[i].width,enemy[i].height);
					batch.draw(redImg, enemy[i].x+7, enemy[i].y+70,50,10);
					batch.draw(greenImg, enemy[i].x+7, enemy[i].y+70,enemy[i].health,10);
				}
			}

			batch.draw(playerImage, player.x, player.y,player.width,player.height);
			if(shooting==true){
				batch.draw(mouth,player.x, player.y,player.width,player.height);
			}
			for(int i=0;i<100;i++){
				if(bullet[i]!=null) {
					batch.draw(bulletImg, bullet[i].x, bullet[i].y, bullet[i].width, bullet[i].height);
				}
			}
			green.x=player.x+player.width/2-50;
			green.y=player.y+player.height+22;
			batch.draw(redImg, green.x, green.y);
			batch.draw(greenImg, green.x, green.y,green.width,green.height);
			batch.end();
			movement();
			enemyMoving();

			for(int i=0;i<50;i++){
				if(enemy[i]!=null){
					if (isColliding(player,enemy[i])&&jump==false){
						playerHit();
					}
				}
			}

			timer++;
			shootingtimer++;

			numberenemy=0;
			for (int i=0;i<enemy.length;i++){
				if(enemy[i]!=null){
					numberenemy=1;
				}
			}
			if(numberenemy==0){
				round++;
				allenemyvel+=0.5;
				for(int i=0;i<5*round;i++){
					spawnEnemy();
					numberenemy++;
				}
			}
		}else if (died==true){
			batch.begin();
			batch.draw(deadscreen,0,0,width,height);
			batch.draw(select,width/4, sy,width,height);
			batch.end();
			selected=setSelectDied();
			diedHandleInput();
		}
	}

	@Override
	public void dispose() {
		// dispose of all the native resources
		playerImage.dispose();
		backgroundMusic.dispose();
		batch.dispose();
	}

	public  void spawnEnemy(){
		for(int i=0;i<50;i++){
			if(enemy[i]==null) {
				enemy[i]=new Enemy();
				double ran=(int)(Math.random()*4);
				if(ran==0){
					enemy[i].x = (player.x-Gdx.graphics.getWidth()/2)+(float)(Math.random()*(Gdx.graphics.getWidth()+200))-200;
					enemy[i].y =screenheight+200;
				}else if(ran==1){
					enemy[i].x = screenwidth+200;
					enemy[i].y = (player.y-Gdx.graphics.getHeight()/2)+(float)(Math.random()*(Gdx.graphics.getHeight()+200))-200;
				}else if(ran==2){
					enemy[i].x = (player.x-Gdx.graphics.getWidth()/2)+(float)(Math.random()*(Gdx.graphics.getWidth()+200))-200;
					enemy[i].y = (player.y-Gdx.graphics.getHeight()/2)-200;
				}else if(ran==3){
					enemy[i].x = (player.x-Gdx.graphics.getWidth()/2)-200;
					enemy[i].y = (player.y-Gdx.graphics.getHeight()/2)+(float)(Math.random()*(Gdx.graphics.getHeight()+200))-200;
				}
				i = 50;
			}
		}
	}

	public void jump(){
		if(player.width<(128+(20*jumpvel))&&fall==false){
			playerImage = new Texture(Gdx.files.internal("playerjump.png"));
			player.width+=jumpvel;
			player.height+=jumpvel;
			player.x-=jumpvel/2;
			player.y-=jumpvel/2;
			if(player.width>=(Gdx.graphics.getWidth()/15+(20*jumpvel))){
				fall=true;
			}
		}else if(fall==true){

			player.width-=jumpvel;
			player.height-=jumpvel;
			player.x+=jumpvel/2;
			player.y+=jumpvel/2;
			green.y-=jumpvel/2;
			if(player.width==Gdx.graphics.getWidth()/15){
				fall=false;
				jump=false;
				playerImage = new Texture(Gdx.files.internal("player0.png"));
			}
		}
	}
	public void  running(){
		framecounter++;
		if(jump==false&&frame==0&&framecounter%5==0){
			frame=1;
			playerImage = new Texture(Gdx.files.internal("player1.png"));
		}else if(jump==false&&frame==1&&framecounter%5==0){
			frame=0;
			playerImage = new Texture(Gdx.files.internal("player0.png"));
		}
	}

	public void movement(){
		if((Gdx.input.isKeyPressed(Keys.LEFT)||Gdx.input.isKeyPressed(Keys.A))){
			player.x -= 10;
			dir=3;
			running();
		}
		if((Gdx.input.isKeyPressed(Keys.RIGHT)||Gdx.input.isKeyPressed(Keys.D))){
			player.x += 10;
			dir=1;
			running();
		}
		if((Gdx.input.isKeyPressed(Keys.DOWN)||Gdx.input.isKeyPressed(Keys.S))){
			player.y -= 10;
			dir=2;
			running();
		}
		if((Gdx.input.isKeyPressed(Keys.UP)||Gdx.input.isKeyPressed(Keys.W))){
			player.y += 10;
			dir=0;
			running();
		}

		if(Gdx.input.isKeyPressed(Keys.SPACE)&&jump==false){
			jump=true;
		}
		if(jump==true){
			jump();
		}
		if((Gdx.input.isButtonPressed(Input.Buttons.LEFT))&&shootingtimer>-1) {
			shootingtimer=0;
			shooting = true;
			for(int i=0;i<100;i++){
				if(bullet[i]==null){
					bullet[i]=new Bullet();
					bulletcounter=i;
					i=100;
				}
			}
			bullet[bulletcounter].x=player.x+player.width/2;
			bullet[bulletcounter].y=player.y+player.height/2-10;
			bullet[bulletcounter].velx = 20*(float) ((double) (mx - bullet[bulletcounter].x) / Math.sqrt((double) (((mx - bullet[bulletcounter].x) * (mx - bullet[bulletcounter].x)) + ((my - bullet[bulletcounter].y) * (my - bullet[bulletcounter].y)))));
			bullet[bulletcounter].vely = 20*(float) ((double) (my - bullet[bulletcounter].y) / Math.sqrt((double) (((mx - bullet[bulletcounter].x) * (mx - bullet[bulletcounter].x)) + ((my - bullet[bulletcounter].y) * (my - bullet[bulletcounter].y)))));
		}else{
			shooting=false;
		}
		playerShooting();
	}

	public void enemyMoving(){
		for(int i=0;i<50;i++){
			if(enemy[i]!=null){
				enemy[i].velx = allenemyvel*(float) ((double) (player.x - enemy[i].x) / Math.sqrt((double) (((player.x - enemy[i].x) * (player.x - enemy[i].x)) + ((player.y - enemy[i].y) * (player.y - enemy[i].y)))));
				enemy[i].vely = allenemyvel*(float) ((double) (player.y - enemy[i].y) / Math.sqrt((double) (((player.x - enemy[i].x) * (player.x - enemy[i].x)) + ((player.y - enemy[i].y) * (player.y - enemy[i].y)))));
				enemy[i].x+=enemy[i].velx;
				enemy[i].y+=enemy[i].vely;
			}
		}
	}

	public boolean isColliding(Rectangle ob1,Enemy ob2){
		if(ob1.x+ob1.width-10>ob2.x&&ob1.x+10<ob2.x+ob2.width&&ob1.y+ob1.width-10>ob2.y&&ob1.y+10<ob2.y+ob2.width){
			return true;
		}else{
			return false;
		}
	}
	public boolean isColliding(Bullet ob1,Enemy ob2){
		if(ob1.x+ob1.width-10>ob2.x&&ob1.x+10<ob2.x+ob2.width&&ob1.y+ob1.width-10>ob2.y&&ob1.y+10<ob2.y+ob2.width){
			return true;
		}else{
			return false;
		}
	}

	public void playerHit(){
		if(timer>20){
			green.width-=10;
			life-=10;
			timer=0;
		}
		if(life==0){
			game=false;
			create();
			died=true;
		}
	}

	public boolean outOfGame(Bullet ob){
		if(ob.x<(player.x-Gdx.graphics.getWidth()/2)-20||ob.x>screenwidth+20||ob.y<(player.y-Gdx.graphics.getHeight()/2)||ob.y>screenheight+20){
			return true;
		}else{
			return false;
		}
	}

	public void playerShooting() {
		for(int i=0;i<100;i++){
			if(bullet[i]!=null) {
				bullet[i].x += bullet[i].velx;
				bullet[i].y += bullet[i].vely;
				for(int j=0;j<50;j++){
					if(enemy[j]!=null&&bullet[i]!=null){
						if (isColliding(bullet[i], enemy[j]) &&jump==false) {
							bullet[i] = null;
							enemyHit(j);
						}
					}if(bullet[i]!=null&&outOfGame(bullet[i])){
						bullet[i]=null;
					}
				}
			}
		}
	}

	public void enemyHit(int index){
		enemy[index].health-=25;
		if(enemy[index].health==0){
			enemy[index]=null;
			numberenemy--;
		}
	}
	public int setSelect(){
		int ret=0;
		if (my<height/3*2&&my>height/5*2){
			ret=1;
			sy=(int)-height/5;
		}else if(my<=height/5*2){
			ret=2;
			sy=(int)-height/5*2;
		}else{
			sy=0;
		}
		return ret;
	}
	public int setSelectDied(){
		int ret=0;
		if(my<height/5*2){
			ret=1;
			sy=(int)-height/5*2;
		}else{
			sy=(int)-height/4;
		}
		return ret;
	}
	public void homeHandleInput(){
		if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
			if (selected == 0) {
				home = false;
				create();
				game = true;
			} else if (selected==1) {
				if(musicplaying==true) {
					backgroundMusic.dispose();
					musicplaying = false;
					onx = -1000;
					offx = 0;
				}else{
					backgroundMusic.play();
					musicplaying=true;
					onx=0;
					offx=-1000;
				}
			}else{
				Gdx.app.exit();
			}
		}
	}
	public void diedHandleInput() {
		if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
			if (selected == 0) {
				died = false;
				game = true;
			} else if (selected == 1) {
				died = false;
				home = true;
			}
		}
	}
}