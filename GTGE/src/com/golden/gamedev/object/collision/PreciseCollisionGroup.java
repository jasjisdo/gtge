/*
 * Copyright (c) 2008 Golden T Studios.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.golden.gamedev.object.collision;

// GTGE
import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.PlayField;


/**
 * <p>Subclass of <code>CollisionGroup</code>
 * that calculates the precise positions of the <code>Sprite</code>s at the
 * moment of collision. It is suitable for collisions that need the colliding
 * objects to stop rather than vanish.</p>
 *
 * <p>For example:
 * <ul><li>Collisions between balls that need to stop or bounce precisely
 * off each other.</li>
 * <li>An object falling to the ground and stopping.</li>
 * <li>Objects that are replaced by something else (such as an explosion effect).</li>
 * & </ul>
 * </p>
 *
 * <p>This class may not work as expected with concave sprites- such as L-shapes. The position
 * of the collision will be found accurately, but the direction may not be as anticipated
 * as it is based on the <code>CollisionRect</code>s rather than pixel
 * collisions or custom <code>CollisionShape</code>s defined in subclasses of <code>PreciseCollisionGroup</code>
 * If concave sprites are necessary, it might be advisable to break them into groups of
 * smaller convex <code>Sprite</code>s.</p>
 *
 * @see PlayField#addCollisionGroup(SpriteGroup, SpriteGroup, CollisionManager)
 *
 */
public abstract class PreciseCollisionGroup extends CollisionGroup {

    /**********
     * This is used to test for non-convergence in pixel perfect collision,
     * or when unusual <code>CollisionShape</code>s are used.
     * The default value is 0.000001.
     *************/
    protected static double ITERATIVE_BAILOUT=0.000001;

    /***********
     * This is the distance that two objects must be within
     * to be considered adjacent. When a collision occurs, the <Sprite>s
     * at their reverted
     * positions are guaranteed to be at least this close. This should be larger than
     * <code>ITERATIVE_BAILOUT</code>.
     * The default value is 0.0001.
     *************/
    protected static double ADJACENCY_TOLERANCE=0.0001;

    //These are used to test if there was a collision before movement
    //with the iterative method
    private CollisionShape shape3;
    private CollisionShape shape4;

    /*******
     * When set true, this <code>PreciseCollisionGroup</code> will send
     * debugging information to the console.
     *************/
    protected boolean log=false;

    /***********
     * Default constructor.
     *************/
    public PreciseCollisionGroup() {
        //sets log to false;
        //set true for debugging
        //log=true;
    }

    /**
     * Performs collision check between Sprite <code>s1</code> and
     * Sprite <code>s2</code>, and returns true if the sprites
     * (<code>shape1</code>, <code>shape2</code>) collided. <p>
     *
     * The revert positions are set precisely by this method.
     *
     * @param	s1	sprite from group 1
     * @param	s2	sprite from group 2
     * @param	shape1	bounding box of sprite 1
     * @param	shape2	bounding box of sprite 2
     * @return	<code>true</code> if the sprites is collided one another.
     */
    public boolean isCollide(Sprite s1, Sprite s2,
            CollisionShape shape1, CollisionShape shape2) {

        //if (shape1.intersects(shape2)) {
        if( (pixelPerfectCollision && isPixelCollide(s1.getX(), s1.getY(), s1.getImage(), s2.getX(), s2.getY(), s2.getImage()))
        || (!pixelPerfectCollision && shape1.intersects(shape2)) ) {
            // basic check to see if collision occurred
            sprite1 = s1; sprite2 = s2;
            collisionSide = 0;

            // set up collision variables

            // this gets the speed
            double speedX1 = s1.getX() - s1.getOldX(),
                    speedY1 = s1.getY() - s1.getOldY(),
                    speedX2 = s2.getX() - s2.getOldX(),
                    speedY2 = s2.getY() - s2.getOldY();
            //now get the bounds for the CollisionShapes
            double x1=shape1.getX()-speedX1,
                    y1=shape1.getY()-speedY1,
                    x2=shape2.getX()-speedX2,
                    y2=shape2.getY()-speedY2;
            double w1=shape1.getWidth(),
                    h1=shape1.getHeight(),
                    w2=shape2.getWidth(),
                    h2=shape2.getHeight();

            if(log) System.out.print("Collision ("+s1.getX()+","+s1.getY()+"),("+x1+","+y1+")-->");

            //check for collision at old location

            if(checkCollisionHelper(s1,s2,x1,y1,x2,y2,true)) {//collision at old location

                //if(!log) {log=true;System.out.println("Collision ("+s1.getX()+","+s1.getY()+"),("+x1+","+y1+")-->");}

                if(log) System.out.print("Overlap->");

                collisionSide=0;

                Sprite spriteToMove, otherSprite;

                if(speedX1==0 && speedY1 ==0 && speedX2==0 && speedY2==0) {//both stationary
                    if(log) System.out.println("Both stationary");
                    //this should only occur when they are placed directly overtop with no movement
                    //behaviour here: leave them alone.
                    return false;
                } else {//find fastest moving
                    //find centres
                    double s1cx=shape1.getX()+shape1.getWidth()/2;
                    double s1cy=shape1.getY()+shape1.getHeight()/2;
                    double s2cx=shape2.getX()+shape2.getWidth()/2;
                    double s2cy=shape2.getY()+shape2.getHeight()/2;

                    if(Math.pow(speedX1,2) +Math.pow(speedY1,2) >
                            Math.pow(speedX2,2) +Math.pow(speedY2,2)) {//sprite 1 faster
                        spriteToMove=s1;
                        otherSprite=s2;
                    } else {
                        spriteToMove=s2;
                        otherSprite=s1;
                    }
                    if(log) System.out.print(spriteToMove+"-->");

                    //find distances to move (based on default collision shapes)
                    //this might need to be changed to use the iterative method
                    //if this behaviour should respect pixel perfection
                    double distXLeft=s1cx-s2cx+w1/2+w2/2;
                    double distXRight=s2cx-s1cx+w1/2+w2/2;
                    double distYUp=s1cy-s2cy+h1/2+h2/2;
                    double distYDown=s2cy-s1cy+h1/2+h2/2;

                    //find minimum distance
                    double minDist=Math.min(Math.min(distXLeft,distXRight),Math.min(distYUp,distYDown));

                    if(spriteToMove==s1) {//move sprite1
                        collisionX2=s2.getX();
                        collisionY2=s2.getY();
                        if(minDist==distXLeft) {collisionX1=s1.getX()-distXLeft;collisionY1=s1.getY();collisionSide=RIGHT_LEFT_COLLISION;} else if(minDist==distXRight) {collisionX1=s1.getX()+distXRight;collisionY1=s1.getY();collisionSide=LEFT_RIGHT_COLLISION;} else if(minDist==distYUp) {collisionX1=s1.getX();collisionY1=s1.getY()-distYUp;collisionSide=BOTTOM_TOP_COLLISION;} else {collisionX1=s1.getX();collisionY1=s1.getY()+distYDown;collisionSide=TOP_BOTTOM_COLLISION;}
                        if(log) System.out.println("Corrected");
                        return true;
                    } else {//move sprite 2
                        collisionX1=s1.getX();
                        collisionY1=s1.getY();
                        if(minDist==distXLeft) {collisionX2=s2.getX()-distXLeft;collisionY2=s2.getY();collisionSide=LEFT_RIGHT_COLLISION;} else if(minDist==distXRight) {collisionX2=s2.getX()+distXRight;collisionY2=s2.getY();collisionSide=RIGHT_LEFT_COLLISION;} else if(minDist==distYUp){collisionX2=s2.getX();collisionY2=s2.getY()-distYUp;collisionSide=TOP_BOTTOM_COLLISION;} else {collisionX2=s2.getX();collisionY2=s2.getY()+distYDown;collisionSide=BOTTOM_TOP_COLLISION;}
                        if(log) System.out.println("Corrected");
                        return true;
                    }



                }

            }			//if overlap

            else { //no collision at old location

                double tHoriz=999999.0,tVert=999999.0; //garbage values that should not be achieved
                int xCollision=-1, yCollision=-1;

                if(speedX1>speedX2) {//left-to-right on X
                    if(log) System.out.print("dx1>dx2-->");
                    tHoriz=(x2-x1-w1)/(speedX1-speedX2);
                    xCollision=RIGHT_LEFT_COLLISION;
                } else if(speedX2>speedX1){ //right-to-left on X
                    if(log) System.out.print("dx1<dx2-->");
                    tHoriz=(x1-x2-w2)/(speedX2-speedX1);
                    xCollision=LEFT_RIGHT_COLLISION;
                }

                if(speedY1>speedY2) {//bottom-to-top on Y
                    if(log) System.out.print("dy1>dy2-->");
                    tVert=(y2-y1-h1)/(speedY1-speedY2);
                    yCollision=BOTTOM_TOP_COLLISION;
                } else if(speedY2>speedY1){ //top-to-bottom on Y
                    if(log) System.out.print("dy1<dy2-->");
                    tVert=(y1-y2-h2)/(speedY2-speedY1);
                    yCollision=TOP_BOTTOM_COLLISION;
                }

                //completely stationary case should have been dealt with above

                double finalT;

                if(tHoriz<=tVert) {//X collision happens first
                    if(log) System.out.print("X "+tHoriz+"-->");

                    collisionSide=xCollision;
                    //check to see if this is actual collision or too early.
                    //may happen when (for example):
                    //    ***
                    //       ^
                    //       |
                    //      sss
                    //      sss
                    //      sss
                    //or similar. If not adjacent at this earliest position,
                    //assume other is correct.

                    if(checkAdjacencyHelper(s1, s2, x1+tHoriz*speedX1, y1+tHoriz*speedY1,
                            x2+tHoriz*speedX2, y2+tHoriz*speedY2, speedX1, speedY1, speedX2,
                            speedY2, false)) {
                        //Yes- X collision is the first real collision
                        if(log) System.out.print("X "+tHoriz+"-->");
                        finalT=tHoriz;
                    } else {
                        //No- X collision is not an actual collision, so use Y collision.
                        if(log) System.out.print("Y "+tVert+"-->");
                        collisionSide=yCollision;
                        finalT=tVert;
                    }
                } else {//Y collision happens first
                    if(log) System.out.print("Y "+tVert+"-->");

                    collisionSide=yCollision;
                    //similar check here
                    if(checkAdjacencyHelper(s1, s2, x1+tVert*speedX1, y1+tVert*speedY1,
                            x2+tVert*speedX2, y2+tVert*speedY2, speedX1, speedY1, speedX2,
                            speedY2, false)) {
                        //Yes- Y collision is the first real collision
                        if(log) System.out.print("Y "+tVert+"-->");
                        finalT=tVert;
                    } else {
                        //No- Y collision is not an actual collision, so use X collision.
                        if(log) System.out.print("X "+tHoriz+"-->");
                        collisionSide=xCollision;
                        finalT=tHoriz;
                    }
                }

                //set revert positions
                //these may be changed later but are
                //correct for simple cases

                //these are for the CollisionRect
                collisionX1=x1+finalT*speedX1;
                collisionY1=y1+finalT*speedY1;
                collisionX2=x2+finalT*speedX2;
                collisionY2=y2+finalT*speedY2;

                //this is sufficient for non-pixel perfect collisions with
                //bounding rectangles

                if(checkCollisionHelper(s1,s2,collisionX1,collisionY1,collisionX2,collisionY2,true)) {
                    //still a collision- this occurs if a non-rectangular CollisionShape exists
                    //larger than its height and width would suggest.

                    if(log) System.out.print("Iterate (1)-->");

                    if( iterativeMethod(s1, s2, 0.0, finalT, x1, y1, x2, y2,
                            speedX1, speedY1, speedX2, speedY2) ) {
                        //collision occurred- collision positions set in iterativeMethod()
                        //correct them because these are for the rect, not the sprite
                        collisionX1=collisionX1-x1+s1.getOldX();
                        collisionY1=collisionY1-y1+s1.getOldY();
                        collisionX2=collisionX2-x2+s2.getOldX();
                        collisionY2=collisionY2-y2+s2.getOldY();

                        if(log) System.out.println("true: "+collisionSide+" ("+collisionX1+","+collisionY1+")");
                        return true;
                    } else {
                        //no actual collision (due to custom collisionShapes)
                        if(log) System.out.println("false");
                        return false;
                    }
                }

                else if(checkAdjacencyHelper(s1,s2,collisionX1,collisionY1,collisionX2,collisionY2,
                        speedX1, speedY1, speedX2, speedY2,true)) {
                    //this occurs when regular bounding boxes are used. Nothing more needs to be done.
                    //need to correct collision?? positions because these are for the rect, not the sprite
                    collisionX1=collisionX1-x1+s1.getOldX();
                    collisionY1=collisionY1-y1+s1.getOldY();
                    collisionX2=collisionX2-x2+s2.getOldX();
                    collisionY2=collisionY2-y2+s2.getOldY();

                    if(log) System.out.println("true: "+collisionSide+" ("+collisionX1+","+collisionY1+")");

                    return true;

                }

                else {
                    //this occurs when the bouding shape is smaller than its width and height
                    //would suggest,
                    //or with pixel perfect collision.

                    if(log) System.out.print("Iterate (2)-->");

                    if( iterativeMethod(s1, s2, finalT, 1.0, x1, y1, x2, y2,
                            speedX1, speedY1, speedX2, speedY2) ) {
                        //collision occurred- collision positions set in iterativeMethod()
                        //correct them because these are for the rect, not the sprite
                        collisionX1=collisionX1-x1+s1.getOldX();
                        collisionY1=collisionY1-y1+s1.getOldY();
                        collisionX2=collisionX2-x2+s2.getOldX();
                        collisionY2=collisionY2-y2+s2.getOldY();

                        if(log) System.out.println("true: "+collisionSide+" ("+collisionX1+","+collisionY1+")");
                        return true;
                    } else {
                        //no actual collision due to small collisionShape or transparent pixels
                        if(log) System.out.println("false");
                        return false;
                    }
                }


            }//no overlap
        } //no collision
        return false;
    } //end of method

    //This checks for an overlap
    protected boolean checkCollisionHelper(Sprite s1, Sprite s2, double x1, double y1,
            double x2, double y2, boolean includePixelPerfect) {

        if(includePixelPerfect && pixelPerfectCollision) {
            return isPixelCollide(x1, y1, s1.getImage(), x2, y2,
                    s2.getImage());
        } else {//check using normal method
            shape3=getCollisionShape1(s1);
            shape3.setLocation(x1,y1);
            shape4=getCollisionShape2(s2);
            shape4.setLocation(x2,y2);
            return shape3.intersects(shape4);
        }
    }

    //This checks for adjacency
    protected boolean checkAdjacencyHelper(Sprite s1, Sprite s2, double x1, double y1,
            double x2, double y2, double dx1, double dy1, double dx2, double dy2,
            boolean includePixelPerfect) {

        //set up offsets for adjacency
        double dx=0,dy=0;
        if(dx1-dx2<0) {dx=-ADJACENCY_TOLERANCE;} else if(dx1-dx2>0) {dx=ADJACENCY_TOLERANCE;}
        if(dy1-dy2<0) {dy=-ADJACENCY_TOLERANCE;} else if(dy1-dy2>0) {dy=ADJACENCY_TOLERANCE;}

        if(includePixelPerfect && pixelPerfectCollision ) {
            return isPixelCollide(x1+dx, y1+dy, s1.getImage(), x2, y2,
                    s2.getImage());
        } else {//check using default collision shapes
            shape3=getCollisionShape1(s1);
            shape3.setLocation(x1+dx,y1+dy);
            shape4=getCollisionShape2(s2);
            shape4.setLocation(x2,y2);
            //if(log) System.out.print(shape3.getX()+","+shape3.getWidth()+","+shape3.getY()+","+shape3.getHeight()+"-"+shape4.getX()+","+shape4.getWidth()+","+shape4.getY()+","+shape4.getHeight()+"-->");
            //if(log) System.out.print(shape3.intersects(shape4)+"-->");
            return shape3.intersects(shape4);
        }
    }

    //iterates to find the just pre-collision position.

    protected boolean iterativeMethod(Sprite s1, Sprite s2, double lowerT, double higherT,
            double oldX1, double oldY1, double oldX2, double oldY2, double speedX1,
            double speedY1, double speedX2, double speedY2) {
        //set up working t
        double workingT=(lowerT+higherT)/2;

        //set up candidate positions
        double curX1, curY1, curX2, curY2;

        //set up fastest speed- this is used for the bailout condition.
        double maxSpeed=Math.max(Math.max(Math.abs(speedX1),Math.abs(speedY1)),
                Math.max(Math.abs(speedX2),Math.abs(speedY2)));

        while(true) {

            //find current candidate position
            curX1=oldX1+workingT*speedX1;
            curY1=oldY1+workingT*speedY1;
            curX2=oldX2+workingT*speedX2;
            curY2=oldY2+workingT*speedY2;

            if( checkCollisionHelper(s1, s2, curX1, curY1, curX2, curY2, true) ) {
                //collided- need a lower t.
                higherT=workingT;
                workingT=(workingT+lowerT)/2;

                if( (higherT-lowerT)*maxSpeed<ITERATIVE_BAILOUT ) {
                    //got too small without avoiding collision
                    //this should not happen- should be caught
                    //in overlapping code
                    System.err.println("Iterative failure-- too close");
                    break;
                }
            } else if( checkAdjacencyHelper(s1, s2, curX1, curY1, curX2, curY2, speedX1,
                    speedY1, speedX2, speedY2, true) ) {
                //not collided but adjacent- good enough.
                //extra check here to counter the fact that the iterative method
                //may find a solution the wrong side of its starting point
                //when stationary
                collisionX1= Math.abs(curX1-oldX1)>2*ADJACENCY_TOLERANCE ?
                    curX1:oldX1;
                collisionY1= Math.abs(curY1-oldY1)>2*ADJACENCY_TOLERANCE ?
                    curY1:oldY1;
                collisionX2= Math.abs(curX2-oldX2)>2*ADJACENCY_TOLERANCE ?
                    curX2:oldX2;
                collisionY2= Math.abs(curY2-oldY2)>2*ADJACENCY_TOLERANCE ?
                    curY2:oldY2;

                return true;
            } else {
                //not adjacent- need a higher t.

                lowerT=workingT;
                workingT=(workingT+higherT)/2;

                if ( (higherT-lowerT)*maxSpeed<ITERATIVE_BAILOUT) {
                    //got too large without achieving adjacency
                    // this occurs when no collision actually
                    //took place.
                    break;
                }
            }
        }

        return false; //default return- no collision

    }

}