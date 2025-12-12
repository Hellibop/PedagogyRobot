Here we will explain our reasoning and why we did the thing we did, specifically for parts of code which still needs work.

## IF-ELSE
### Basics
IF-ELSE blocks are implemented like the LOOP blocks, one **start block** where u can select a value and a **end block**. For the IF-ELSE blocks there is also an **else block** in between the start and end blocks for if the start blocks condition(value) isn't passed. These blocks are defined for the android app in [RobotActionType.kt](./AIDA-Development-main/AIDA-Development/android/app/src/main/java/com/example/aida/domain/model/RobotActionType.kt) and [ros2_interface.py](./AIDA-Development-main/AIDA-Development/AIDA/ros2_humble_ws/src/aida_api/aida_api/ros2_interface.py) for the robot.

### Conditions
Blocks are made into an [RobotAction.kt](./AIDA-Development-main/AIDA-Development/android/app/src/main/java/com/example/aida/domain/model/RobotAction.kt) object, and since the IF-ELSE blocks are defined as special in [RobotActionType.kt](./AIDA-Development-main/AIDA-Development/android/app/src/main/java/com/example/aida/domain/model/RobotActionType.kt) the **data** string for the object. The **data** string can be anything since each special block is handled seperatly. In the case of the IF-ELSE blocks the **data** string contains a condition type and condition argument in the format of [condition_type : int, condition_arg : string]. Condition types are defined in [IfStatementConditionsType.kt](./AIDA-Development-main/AIDA-Development/android/app/src/main/java/com/example/aida/domain/model/IfStatementConditionsType.kt) on the android side and [if_else_conditions_handler.py](./AIDA-Development-main/AIDA-Development/AIDA/ros2_humble_ws/src/aida_api/aida_api/if_else_conditions_handler.py) for the robot. Just like the blocks if they are defined as special in [IfStatementConditionsType.kt](./AIDA-Development-main/AIDA-Development/android/app/src/main/java/com/example/aida/domain/model/IfStatementConditionsType.kt) they **should** make use of the condtion_arg (They don't right now hehe).  

### Questionable design
When construction the **data** string for IF-ELSE the type and arg is made into a [IfStatementConditions.kt](./AIDA-Development-main/AIDA-Development/android/app/src/main/java/com/example/aida/domain/model/IfStatementConditions.kt) object for type checking and then into the formatted string. This could be seen as redundant and can therefore be removed if you see no use for it.

### Robot
More on the robot side the logic for how the different conditions should be evaluated is not implemented what so ever. The main logic in [ros2_interface.py](./AIDA-Development-main/AIDA-Development/AIDA/ros2_humble_ws/src/aida_api/aida_api/ros2_interface.py) checks with **if_else_condition_handler** defined in [if_else_conditions_handler.py](./AIDA-Development-main/AIDA-Development/AIDA/ros2_humble_ws/src/aida_api/aida_api/if_else_conditions_handler.py), the **data** string is passed to the funciton and depending on if it returns true or false the correct sequence of blocks are processed. 

### UI
In [RobotActionType.kt](./AIDA-Development-main/AIDA-Development/android/app/src/main/java/com/example/aida/domain/model/RobotActionType.kt) we defined the new feature IF/ELSE as an available action type. In [ActionData.kt](./AIDA-Development-main/AIDA-Development/android/app/src/main/java/com/example/aida/ui/component/ActionsData.kt) you can define UI **name** and **icon**. 
[SequenceTabPage.kt](./AIDA-Development-main/AIDA-Development/android/app/src/main/java/com/example/aida/ui/page/SequenceTabPage.kt) is used to define ELSE and IF_END in the same way as LOOP_END where the blocks are grouped together so that all is created when you press IF and LOOP.
In [SequenceBar.kt](AIDA-Development-main/AIDA-Development/android/app/src/main/java/com/example/aida/ui/component/SequenceBar.kt) we defined what should appear once you press the setting button on the IF-block.
We also created a new file [PopupIf.kt](AIDA-Development-main/AIDA-Development/android/app/src/main/java/com/example/aida/ui/popups/PopupIf.kt) in which defines what happens in the popup setting button once it is pressed.
### Problems
Since we didn't have access to the robot for testing we do not know if everyting will be sent and processed correctly. And again the android app does not have the functionality to define the **condition_arg** and is set to "Null" in [PopupIf.kt](./AIDA-Development-main/AIDA-Development/android/app/src/main/java/com/example/aida/ui/popups/PopupIf.kt). The conditions themselfs are again not implemented on the robot side and only retrun staticly true or false.

## Joystick
### Initial bug report
The joystick had a bug in which it did **not reset** to default position upon release. This meant it kept giving the robot information even after the finger pressing on the joystick was released.

### Solution
We implemented a **flag** in [ros2_interface.py](./AIDA-Development-main/AIDA-Development/AIDA/ros2_humble_ws/src/aida_api/aida_api/ros2_interface.py) which tracks the queue of movement requests for the robot. Whenever the queue is empty, we drop the flag and send a **reset request** in which *should* reset the joystick.

### Problem
This code has **not been tested** due to the lack of hardware available. Since there was no robot to test this on, we cannot confirm that it works as intended. It is important that this feature is tested once a robot is available.

