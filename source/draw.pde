void draw() {
  background(0);
  for (Boid boid : flock) {
    boid.edges();
    boid.flock(flock);
    boid.update();
    boid.show();
  }
}
