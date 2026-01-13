from enum import IntEnum

# The extra_data sent with the if_start block contains the condition and
# argument (optional) in the following format:
# [condition : int, condition_arg : any]

# All conditions that can be sent with if_start block
class Conditions(IntEnum):
    true = 1
    false = 2
    camera_condition_with_arg = 3
   
# Handler for if_else block conditions
# condition_arg is an optional argument for conditions that need it
# Returns True or False based on the condition evaluation
def if_else_condition_handler(condition_data : list) -> bool:
    # Validate condition_data
    if not condition_data or not isinstance(condition_data, list) or len(condition_data) < 1:
        return False

    condition = Conditions(condition_data[0])
    condition_arg = condition_data[1] if len(condition_data) > 1 else None

    if condition == Conditions.true:
        return handle_true_condition()
    elif condition == Conditions.false:
        return handle_false_condition()
    elif condition == Conditions.camera_condition_with_arg:
        return handle_camera_condition(condition_arg)

    return False

def handle_true_condition() -> bool:
    return True

def handle_false_condition() -> bool:
    return False

def handle_camera_condition(arg) -> bool:
    # TODO: implement real camera condition logic using `arg`
    return bool(arg)