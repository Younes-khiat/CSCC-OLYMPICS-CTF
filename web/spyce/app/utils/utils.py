
SECRET_MISSION_CODE = "CSCC{n0t_th3_r34l_fl4g}"  

def validate_seat(seat_id):
    return seat_id.isalnum()

def get_status(booking_id):
    return {"status": "confirmed", "id": booking_id}
