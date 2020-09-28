import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import com.sun.jna.platform.win32.User32; 
import com.sun.jna.platform.win32.WinDef.HWND; 
import com.sun.jna.platform.win32.Kernel32; 
import com.sun.jna.platform.win32.WinDef.RECT; 
import com.sun.jna.platform.win32.WinDef.POINT; 
import com.sun.jna.platform.win32.WinUser; 

import com.sun.jna.*; 
import com.sun.jna.internal.*; 
import com.sun.jna.ptr.*; 
import com.sun.jna.win32.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Boids extends PApplet {

// A lot of this code comes from Daniel Shiffman.
// https://thecodingtrain.com/CodingChallenges/124-flocking-boids.html

Boid[] flock;
PShape boidShape;

float maxSpeed;
float maxForce;
float radius;

float alignValue;
float cohesionValue;
float separationValue;

public void setup() {
  //size(1280, 720, OPENGL);
  
  frameRate(60);
  noCursor();
  
  float shapeSize = 3 * width / 1920;
  boidShape = createShape();
  boidShape.beginShape();
  boidShape.strokeWeight(1.5f * width / 1920);
  boidShape.noFill();
  boidShape.stroke(255);
  boidShape.vertex(shapeSize * 4, 0);
  boidShape.vertex(-shapeSize, shapeSize * 2);
  boidShape.vertex(0, 0);
  boidShape.vertex(-shapeSize, -shapeSize * 2);
  boidShape.endShape(CLOSE);
  
  alignValue = 0.3f;
  cohesionValue = 0.5f;
  separationValue = 0.5f;
  
  maxSpeed = 3 * width / 1920;
  maxForce = 0.1f;
  radius = 100;
  
  int n = 500;
  flock = new Boid[n];
  for (int i = 0; i < n; i++) {
    flock[i] = new Boid();
  }
}
// Boid class.

class Boid {
  PVector position;
  PVector velocity;
  PVector acceleration;
  
  Boid() {
    position = new PVector(random(width), random(height));
    velocity = PVector.random2D();
    velocity.setMag(random(1, maxSpeed));
    acceleration = new PVector();
  }

  public void edges() {
    if (position.x > width) {
      position.x = 0;
    } else if (position.x < 0) {
      position.x = width;
    }
    if (position.y > height) {
      position.y = 0;
    } else if (position.y < 0) {
      position.y = height;
    }
  }

  public PVector align(Boid[] boids) {
    PVector steering = new PVector();
    int total = 0;
    for (Boid other: boids) {
      float d = dist(position.x, position.y, other.position.x, other.position.y);
      if (other != this && d < radius) {
        steering.add(other.velocity);
        total++;
      }
    }
    if (total > 0) {
      steering.div(total);
      steering.setMag(maxSpeed);
      steering.sub(velocity);
      steering.limit(maxForce);
    }
    return steering;
  }

  public PVector separation(Boid[] boids) {
    PVector steering = new PVector();
    int total = 0;
    for (Boid other: boids) {
      float d = dist(position.x, position.y, other.position.x, other.position.y);
      if (other != this && d < radius) {
        PVector diff = PVector.sub(position, other.position);
        diff.div(d * d);
        steering.add(diff);
        total++;
      }
    }
    if (total > 0) {
      steering.div(total);
      steering.setMag(maxSpeed);
      steering.sub(velocity);
      steering.limit(maxForce);
    }
    return steering;
  }

  public PVector cohesion(Boid[] boids) {
    PVector steering = new PVector();
    int total = 0;
    for (Boid other: boids) {
      float d = dist(position.x, position.y, other.position.x, other.position.y);
      if (other != this && d < radius) {
        steering.add(other.position);
        total++;
      }
    }
    if (total > 0) {
      steering.div(total);
      steering.sub(position);
      steering.setMag(maxSpeed);
      steering.sub(velocity);
      steering.limit(maxForce);
    }
    return steering;
  }

  public void flock(Boid[] boids) {
    PVector alignment = align(boids);
    PVector cohesion = cohesion(boids);
    PVector separation = separation(boids);

    alignment.mult(alignValue);
    cohesion.mult(cohesionValue);
    separation.mult(separationValue);

    acceleration.add(alignment);
    acceleration.add(cohesion);
    acceleration.add(separation);
  }

  public void update() {
    position.add(velocity);
    velocity.add(acceleration);
    velocity.limit(maxSpeed);
    acceleration.mult(0);
  }
  
  public void drawBoid(float x, float y, float heading) {
    pushMatrix();
    translate(x, y);
    rotate(heading);
    shape(boidShape);
    popMatrix();
  }

  public void show() {
    drawBoid(position.x, position.y, velocity.heading());
    
    if (position.x < 50) {
      drawBoid(position.x + width, position.y, velocity.heading());
    }
    if (position.x > width - 50) {
      drawBoid(position.x - width, position.y, velocity.heading());
    }
    if (position.y < 50) {
      drawBoid(position.x, position.y + height, velocity.heading());
    }
    if (position.y > height - 50) {
      drawBoid(position.x, position.y - height, velocity.heading());
    }
  }
}































//
public void draw() {
  background(0);
  for (Boid boid : flock) {
    boid.edges();
    boid.flock(flock);
    boid.update();
    boid.show();
  }
}
/*
 * Purpose: turns an exported application from Processing IDE into a screen saver for Windows. 
 * 
 * HOW TO USE: create your animation as usual using the main tab, and once you are ready to export
 * the application, rename the function below to 'main(string[] args), then export the application as 
 * usual (File -> Export Application).
 * 
 * LIMITATIONS: including a custom "main" function, as exemplified in this file, will break Processing
 * file loading operations, such as loadImage() and loadShader(), and the sketch will not run from the IDE.
 * Two possible workarounds: 1)  remove or rename the main function 2) use an absolute path for the file
 * loading operations.
 */









/**
 * Rename to 'main' before exporting the application.
 * 
 */
static public void main(String[] args) {


  //Processing specific arguments. The last argument must match the sketch name, otherwise you get a ClassNotFoundException
  String[] appletArgs = new String[] {   "--window-color=#666666", "--hide-stop", "Boids"};

  if (args == null || args.length == 0)
  {
    //no additional arguments passed to the Processing application.
    //this happens e.g. if you double-click the .exe file in Windows explorer 
    //or run it from command line without any arguments.
    PApplet.main(appletArgs);
    return;
  }
  String firstArgument = args[0].toLowerCase().trim();
  String secondArgument = null;

  // Handle cases where arguments are separated by colon. Examples: /c:1234567 or /P:1234567
  if (firstArgument.length() > 2)
  {
    secondArgument = firstArgument.substring(3).trim();
    firstArgument = firstArgument.substring(0, 2);
  } else if (args.length > 1)
    secondArgument = args[1];


  if (firstArgument.equals("/c")) // Screen saver is running in Configuration mode (you pressed "Settings..." in Windows screen saver settings dialog)
  {      
    //Here you could add code to show a configuration window to change the parameters of the sketch.
    //In this demo we show only a message box and return
    String infoMessage = "This screen-save does not have configurable settings.";
    javax.swing.JOptionPane.showMessageDialog(null, infoMessage, "Configuration", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    return;
  } else if (firstArgument.equals("/p")) {      //Screen saver is running in Preview mode. In this case, windows passes also a second argument to the sketch
    if (secondArgument == null)
    {
      String err = "Sorry, but the expected window handle was not provided.";          
      javax.swing.JOptionPane.showMessageDialog(null, err, "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
      return;
    }
    PApplet.main(appletArgs);

    //Source: http://www.rgagnon.com/javadetails/java-detect-if-a-program-is-running-using-jna.html

    HWND hwndChild = User32.INSTANCE.FindWindow("_newt_clazz0", null); //windows class for the Processing sketch


    HWND hwndParent = new HWND((new User32.INT_PTR(Integer.parseInt(secondArgument))).toPointer());


    RECT ParentRect = new RECT();
    User32.INSTANCE.GetWindowRect(hwndParent, ParentRect);


    //https://msdn.microsoft.com/en-us/library/windows/desktop/ms633541(v=vs.85).aspx
    User32.INSTANCE.SetWindowLong(hwndChild, WinUser.GWL_STYLE, 0); //keeps all existing styles, minus WS_CAPTION
    User32.INSTANCE.SetWindowLong(hwndChild, WinUser.GWL_STYLE, WinUser.WS_CHILDWINDOW | WinUser.WS_VISIBLE ); //keeps all existing styles, minus WS_CAPTION

    //SetWindowsPos:https://msdn.microsoft.com/en-us/library/windows/desktop/ms633545(v=vs.85).aspx
    HWND zero = new HWND((new User32.INT_PTR(Integer.parseInt("0", 16))).toPointer()); //replaces constant HWND_TOP (0): Places the window at the top of the Z order.
    User32.INSTANCE.SetParent(hwndChild, hwndParent);
    User32.INSTANCE.SetWindowPos(hwndChild, zero, 0, 0, ParentRect.right-ParentRect.left, ParentRect.bottom-ParentRect.top, 0x0040);
  } else if (firstArgument.equals("/s"))      // Full-screen mode
  {
    PApplet.main(concat(appletArgs, args));
  } else    // Undefined argument
  {
    String infoMessage = "Sorry, but the command line argument \"" + firstArgument + "\" is not valid.";
    javax.swing.JOptionPane.showMessageDialog(null, infoMessage, "Error", javax.swing.JOptionPane.INFORMATION_MESSAGE);
  }
}


/* Handles logic and events to make the application exit during mouse moved, mouse pressed or key pressed */
int lastMouseMoved = 0;

public void mouseMoved() {
  if (lastMouseMoved == 0) {
    lastMouseMoved = millis();
    return;
  }
  if (millis() - lastMouseMoved > 200) {
    println("Exiting on mouseMoved()");
    exit();
  } else {

    lastMouseMoved = millis();
  }
}

public void mousePressed() {
  exit();
}

public void keyPressed() {
  println("Exiting on keyPressed()");
  exit();
}
    public void settings() {  fullScreen(); }
}
