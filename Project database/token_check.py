from sqlalchemy_utils import database_exists
from data_base import *
"""
Gets a token and puts it in the black list.
"""
def check_token():
    token = request.headers.get('Authorization')
    if not token:
        return "",400
    tokens = Blacklist_db.query.all()

    for elem in tokens:
        if str(token) == str(elem):
            return "",422
    return True
