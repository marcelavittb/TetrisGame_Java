import java.awt.Color;

public class Mino {
    
    public Block b[] = new Block[4];
    public Block tempB[] = new Block[4];
    int autoDropCounter = 0;
    public int direction = 1; //0: spawn, 1: right, 2: down, 3: left
    boolean leftCollision, rightCollision, bottomCollision;
    public boolean active = true;
    public boolean deactivating;
    int deactivateCounter = 0;

    public void create(Color c){
        b[0] = new Block(c);
        b[1] = new Block(c);
        b[2] = new Block(c);
        b[3] = new Block(c);
            tempB[0] = new Block(c);
            tempB[1] = new Block(c);
            tempB[2] = new Block(c);
            tempB[3] = new Block(c);
        }

        public void setXY(int x, int y){}
        public void updateXY(int direction){

            checkRotationCollision();
            if ( leftCollision == false && rightCollision  == false && bottomCollision == false ){
                this.direction = direction;
                b[0].x = tempB[0].x;
                b[0].y = tempB[0].y;
                b[1].x = tempB[1].x;
                b[1].y = tempB[1].y;
                b[2].x = tempB[2].x;
                b[2].y = tempB[2].y;
                b[3].x = tempB[3].x;
                b[3].y = tempB[3].y;
            }
        }
        public void getdirection1(){}
        public void getdirection2(){}
        public void getdirection3(){}
        public void getdirection4(){}
        public void checkMovementCollision(){

            leftCollision = false;
            rightCollision = false;
            bottomCollision = false;
            checkStaticBlockCollision();
            //Check left collision
            for(int i=0; i< b.length; i++){
                if(b[i].x == PlayManager.left_x){
                    leftCollision = true;
                }
            }
            //Check right collision
            for(int i=0; i< b.length; i++){
                if(b[i].x + Block.SIZE == PlayManager.right_x){
                    rightCollision = true;
                }
            }
            //Check bottom collision
            for(int i=0; i< b.length; i++){
                if(b[i].y + Block.SIZE == PlayManager.bottom_y){
                    bottomCollision = true;
                }
            }
        }
        public void checkRotationCollision(){
              leftCollision = false;
            rightCollision = false;
            bottomCollision = false;

            checkStaticBlockCollision();

            //Check left collision
            for(int i=0; i< b.length; i++){
                if(tempB[i].x < PlayManager.left_x){
                    leftCollision = true;
                }
            }
            //Check right collision
            for(int i=0; i< b.length; i++){
                if(tempB[i].x + Block.SIZE > PlayManager.right_x){
                    rightCollision = true;
                }
            }
            //Check bottom collision
            for(int i=0; i< b.length; i++){
                if(tempB[i].y + Block.SIZE > PlayManager.bottom_y){
                    bottomCollision = true;
                }
            }
        }
        private void checkStaticBlockCollision(){
            for (int j = 0; j < PlayManager.staticBlocks.size(); j++) {
                Block staticBlock = PlayManager.staticBlocks.get(j);
                //Check left collision
                for(int i=0; i< b.length; i++){
                    if(b[i].x - Block.SIZE == staticBlock.x && b[i].y == staticBlock.y){
                        leftCollision = true;
                    }
                }
                //Check right collision
                for(int i=0; i< b.length; i++){
                    if(b[i].x + Block.SIZE == staticBlock.x && b[i].y == staticBlock.y){
                        rightCollision = true;
                    }
                }
                //Check bottom collision
                for(int i=0; i< b.length; i++){
                    if(b[i].y + Block.SIZE == staticBlock.y && b[i].x == staticBlock.x){
                        bottomCollision = true;
                    }
                }
            }
        }
        public void update(){

            if (deactivating){
                deactiavating();
            }
            //Move the mino
            if(KeyHandler.upPressed){
                //rotation
                    switch(direction){
                        case 1:
                            getdirection2();
                            break;
                        case 2:
                            getdirection3();
                            break;
                        case 3:
                            getdirection4();
                            break;
                        case 4:
                            getdirection1();
                            break;
                    }
                KeyHandler.upPressed = false;

            }

            checkMovementCollision();

            if(KeyHandler.downPressed){
                if (bottomCollision ==false){    
                //move down
                b[0].y += Block.SIZE;
                b[1].y += Block.SIZE;
                b[2].y += Block.SIZE;
                b[3].y += Block.SIZE;

                autoDropCounter = 0;
                }
                KeyHandler.downPressed = false;
            }
            if(KeyHandler.leftPressed){

                if (leftCollision == false){
                //move left
                b[0].x -= Block.SIZE;
                b[1].x -= Block.SIZE;
                b[2].x -= Block.SIZE;
                b[3].x -= Block.SIZE;
                }

                KeyHandler.leftPressed = false;
            }
            if(KeyHandler.rightPressed){
                if (rightCollision == false){
                //move right
                b[0].x += Block.SIZE;
                b[1].x += Block.SIZE;
                b[2].x += Block.SIZE;
                b[3].x += Block.SIZE;
                }
                KeyHandler.rightPressed = false;
            }
            if (bottomCollision) {
                //the mino has landed
                deactivating = true;
            }else{
                autoDropCounter++;
                if(autoDropCounter == PlayManager.dropInterval){
                //the mino drops down by one block
                    b[0].y += Block.SIZE;
                    b[1].y += Block.SIZE;   
                    b[2].y += Block.SIZE;
                    b[3].y += Block.SIZE;
                    autoDropCounter = 0;
                }
            }


        }

        private void deactiavating() {
            deactivateCounter++;
            if (deactivateCounter ==45) {
                deactivateCounter = 0;
                checkMovementCollision();

                if(bottomCollision){
                    active = false;
                }
            }

        }

        public void draw(java.awt.Graphics2D g2){

            int margin = 2;
            g2.setColor(b[0].c);
            g2.fillRect (b[0].x, b[0].y, Block.SIZE - (margin*2), Block.SIZE- (margin*2));
            g2.fillRect (b[1].x, b[1].y, Block.SIZE- (margin*2), Block.SIZE- (margin*2));
            g2.fillRect (b[2].x, b[2].y, Block.SIZE- (margin*2), Block.SIZE- (margin*2));
            g2.fillRect (b[3].x, b[3].y, Block.SIZE- (margin*2), Block.SIZE- (margin*2));

        }
}
 