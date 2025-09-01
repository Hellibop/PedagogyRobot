// Pin definitions (TB6600 stepper drivers)
const int LstepPin = 2;
const int LdirPin = 3;
const int LenPin = 4;

const int RstepPin = 5;
const int RdirPin = 6;
const int RenPin = 7;

char command = 's';  // Current command

void setup() {
  Serial.begin(9600);

  pinMode(LstepPin, OUTPUT);
  pinMode(LdirPin, OUTPUT);
  pinMode(LenPin, OUTPUT);

  pinMode(RstepPin, OUTPUT);
  pinMode(RdirPin, OUTPUT);
  pinMode(RenPin, OUTPUT);

  // Disable motors initially
  digitalWrite(LenPin, HIGH);
  digitalWrite(RenPin, HIGH);
}

void loop() {
  if (Serial.available()) {
    command = Serial.read();
  }

  switch (command) {
    case 'f':
      moveForward();
      break;
    case 'b':
      moveBackward();
      break;
    case 'l':
      rotateLeft();
      break;
    case 'r':
      rotateRight();
      break;
    case 'q':
      forwardLeft();
      break;
    case 'e':
      forwardRight();
      break;
    case 'z':
      backwardLeft();
      break;
    case 'c':
      backwardRight();
      break;
    case 's':
    default:
      stopMotors();
      break;
  }
}

// Step functions
void stepMotors(int speedMicros, bool leftEnable, bool rightEnable, bool leftDir, bool rightDir) {
  digitalWrite(LenPin, leftEnable ? LOW : HIGH);
  digitalWrite(RenPin, rightEnable ? LOW : HIGH);
  digitalWrite(LdirPin, leftDir);
  digitalWrite(RdirPin, rightDir);

  digitalWrite(LstepPin, HIGH);
  digitalWrite(RstepPin, HIGH);
  delayMicroseconds(speedMicros);

  digitalWrite(LstepPin, LOW);
  digitalWrite(RstepPin, LOW);
  delayMicroseconds(speedMicros);
}

// Movement behaviors
void moveForward() {
  stepMotors(300, true, true, LOW, HIGH);
}

void moveBackward() {
  stepMotors(300, true, true, HIGH, LOW);
}

void rotateLeft() {
  stepMotors(300, true, true, HIGH, HIGH); // Left back, Right back
}

void rotateRight() {
  stepMotors(300, true, true, LOW, LOW);   // Left forward, Right forward
}

void forwardLeft() {
  stepMotors(300, true, true, HIGH, HIGH); // Left back, Right forward
}

void forwardRight() {
  stepMotors(300, true, true, LOW, LOW);   // Left forward, Right back
}

void backwardLeft() {
  stepMotors(300, true, true, LOW, LOW);   // Same as forwardRight (but slower if needed)
}

void backwardRight() {
  stepMotors(300, true, true, HIGH, HIGH); // Same as forwardLeft (but slower if needed)
}

void stopMotors() {
  digitalWrite(LenPin, HIGH);
  digitalWrite(RenPin, HIGH);
  delay(10);
}
