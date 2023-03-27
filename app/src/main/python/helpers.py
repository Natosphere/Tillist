import cv2 as cv
import numpy as np
import base64
import io
from PIL import Image





def imageProcessing(str):

    img = string_to_image(str)


    # dim_limit = 1080
    # max_dim = max(img.shape)
    # if max_dim > dim_limit:
    #     resize_scale = dim_limit / max_dim
    #     img = cv.resize(img, None, fx=resize_scale, fy=resize_scale)


    # resize


    # grayscale
    img_gray = cv.cvtColor(img, cv.COLOR_BGR2GRAY)

    # remove text (hopefully)
    kernel = np.ones((5,5),np.uint8)
    final_img = cv.morphologyEx(img_gray, cv.MORPH_CLOSE, kernel, iterations= 5)

    return image_to_string(final_img)




def string_to_image(str):
    decoded_data = base64.b64decode(str)
    np_data = np.frombuffer(decoded_data, np.uint8)
    img = cv.imdecode(np_data, cv.IMREAD_UNCHANGED)
    return img

def image_to_string(img):
    pil_im = Image.fromarray(img)
    buff = io.BytesIO()
    pil_im.save(buff, format="PNG")
    img_str = base64.b64encode(buff.getvalue())
    return "" + str(img_str, "utf-8")

