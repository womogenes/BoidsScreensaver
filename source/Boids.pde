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

void setup() {
  //size(1280, 720, OPENGL);
  fullScreen();
  frameRate(60);
  noCursor();
  
  float shapeSize = 3 * width / 1920;
  boidShape = createShape();
  boidShape.beginShape();
  boidShape.strokeWeight(1.5 * width / 1920);
  boidShape.noFill();
  boidShape.stroke(255);
  boidShape.vertex(shapeSize * 4, 0);
  boidShape.vertex(-shapeSize, shapeSize * 2);
  boidShape.vertex(0, 0);
  boidShape.vertex(-shapeSize, -shapeSize * 2);
  boidShape.endShape(CLOSE);
  
  alignValue = 0.3;
  cohesionValue = 0.5;
  separationValue = 0.5;
  
  maxSpeed = 3 * width / 1920;
  maxForce = 0.1;
  radius = 100;
  
  int n = 500;
  flock = new Boid[n];
  for (int i = 0; i < n; i++) {
    flock[i] = new Boid();
  }
}
