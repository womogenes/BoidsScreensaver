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

  void edges() {
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

  PVector align(Boid[] boids) {
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

  PVector separation(Boid[] boids) {
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

  PVector cohesion(Boid[] boids) {
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

  void flock(Boid[] boids) {
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

  void update() {
    position.add(velocity);
    velocity.add(acceleration);
    velocity.limit(maxSpeed);
    acceleration.mult(0);
  }
  
  void drawBoid(float x, float y, float heading) {
    pushMatrix();
    translate(x, y);
    rotate(heading);
    shape(boidShape);
    popMatrix();
  }

  void show() {
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
