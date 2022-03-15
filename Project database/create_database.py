from sqlalchemy_utils import database_exists
from data_base import *
"""
Welcome message for when you go on the webbsite.
"""
@app.route("/")
def welcome():
    return "Welcome!"
"""
Init the data base if its not runnig
"""
@app.route("/init_db",methods = ["GET"])
def init_db():
    if not database_exists('sqlite:///memeroom.db'):
        db.create_all()
        return "DATABASE INIT"
    else:
        #db.drop_all()      #denna ska användas när man kör testfilen
        #db.create_all()    #denna ska användas när man kör testfilen
        return "DATABASE ALREADY IN USE"
