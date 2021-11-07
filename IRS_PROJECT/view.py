import cv2
import matplotlib.pyplot as plt
import matplotlib.image as mpimg
import pygame, time
from pygame.locals import *
import sys

def process(file, path, key):
    # init pygame
    pygame.init()
    # set the window
    screen = pygame.display.set_mode((800, 800))
    white = [255, 255, 255]
    screen.fill(white)

    # set title
    pygame.display.set_caption('user '+str(key)+'\'s related items in '+file+' data')

    # set the font
    font = pygame.font.SysFont("", 30)
    text_surface = font.render("score=", True, (0, 0, 0))

    dirPath = 'poster/'
    dic = {}
    post = {}
    # establish dict
    # establish empty list for each key
    with open(path+file) as f:
         for line in f:
                key1, val = line.split()
                dic[key1] = []
                post[key1] = []

    # insert data of lists for each key
    with open(path+file) as f:
         for line in f:
                key1, val = line.split()
                dic[key1].append(val)


    for key1 in dic.keys():
        for id in dic[key1]:
            name = dirPath + '/' + id + '.jpg'
            img = cv2.imread(name)
            post[key1].append(img)

    # def showimg(key):
    #     for img in post[str(key)]:
    #         plt.imshow(img)
    #         plt.axis('off')
    #         plt.show()

    d0,d1,d2,d3,d4,d5,d6,d7,d8,d9,d10,\
    d11,d12,d13,d14 =[],[],[],[],[],[],[],[],[],[],\
                                         [],[],[],[],[]
    for key1 in dic.keys():
        d_0 = dic[key1][0]
        d0.append(d_0)

        if len(dic[key1]) >= 2:
            d_1 = dic[key1][1]
            d1.append(d_1)

        else:
            d1.append(-1)

        if len(dic[key1]) >= 3:
            d_2 = dic[key1][2]
            d2.append(d_2)

        else:
            d2.append(-1)

        if len(dic[key1]) >= 4:
            d_3 = dic[key1][3]
            d3.append(d_3)

        else:
            d3.append(-1)

        if len(dic[key1]) >= 5:
            d_4 = dic[key1][4]
            d4.append(d_4)

        else:
            d4.append(-1)

        if len(dic[key1]) >= 6:
            d_5 = dic[key1][5]
            d5.append(d_5)

        else:
            d5.append(-1)

        if len(dic[key1]) >= 7:
            d_6 = dic[key1][6]
            d6.append(d_6)

        else:
            d6.append(-1)

        if len(dic[key1]) >= 8:
            d_7 = dic[key1][7]
            d7.append(d_7)

        else:
            d7.append(-1)

        if len(dic[key1]) >= 9:
            d_8 = dic[key1][8]
            d8.append(d_8)

        else:
            d8.append(-1)

        if len(dic[key1]) >= 10:
            d_9 = dic[key1][9]
            d9.append(d_9)

        else:
            d9.append(-1)

        if len(dic[key1]) >= 11:
            d_10 = dic[key1][10]
            d10.append(d_10)

        else:
            d10.append(-1)

        if len(dic[key1]) >= 12:
            d_11 = dic[key1][11]
            d11.append(d_11)

        else:
            d11.append(-1)

        if len(dic[key1]) >= 13:
            d_12 = dic[key1][12]
            d12.append(d_12)

        else:
            d12.append(-1)

        if len(dic[key1]) >= 14:
            d_13 = dic[key1][13]
            d13.append(d_13)

        else:
            d13.append(-1)

        if len(dic[key1]) >= 15:
            d_14 = dic[key1][14]
            d14.append(d_14)

        else:
            d14.append(-1)



    def show(key):
        user = pygame.image.load(dirPath+"user.jpg").convert()
        img1 = pygame.image.load(dirPath + '/' + d0[int(key) - 1] + '.jpg').convert_alpha()
        img2 = pygame.image.load(dirPath + '/' + d1[int(key) - 1] + '.jpg').convert_alpha()
        img3 = pygame.image.load(dirPath + '/' + d2[int(key) - 1] + '.jpg').convert_alpha()
        img4 = pygame.image.load(dirPath + '/' + d3[int(key) - 1] + '.jpg').convert_alpha()
        img5 = pygame.image.load(dirPath + '/' + d4[int(key) - 1] + '.jpg').convert_alpha()
        img6 = pygame.image.load(dirPath + '/' + d5[int(key) - 1] + '.jpg').convert_alpha()
        img7 = pygame.image.load(dirPath + '/' + d6[int(key) - 1] + '.jpg').convert_alpha()
        img8 = pygame.image.load(dirPath + '/' + d7[int(key) - 1] + '.jpg').convert_alpha()
        img9 = pygame.image.load(dirPath + '/' + d8[int(key) - 1] + '.jpg').convert_alpha()
        img10 = pygame.image.load(dirPath + '/' + d9[int(key) - 1] + '.jpg').convert_alpha()
        img11 = pygame.image.load(dirPath + '/' + d10[int(key) - 1] + '.jpg').convert_alpha()
        img12 = pygame.image.load(dirPath + '/' + d11[int(key) - 1] + '.jpg').convert_alpha()
        img13 = pygame.image.load(dirPath + '/' + d12[int(key) - 1] + '.jpg').convert_alpha()
        img14 = pygame.image.load(dirPath + '/' + d13[int(key) - 1] + '.jpg').convert_alpha()
        img15 = pygame.image.load(dirPath + '/' + d14[int(key) - 1] + '.jpg').convert_alpha()


        user = pygame.transform.scale(user, (100, 100))  # adjust size
        img1 = pygame.transform.scale(img1, (100, 147))  # adjust size
        img2 = pygame.transform.scale(img2, (100, 147))  # adjust size
        img3 = pygame.transform.scale(img3, (100, 147))  # adjust size
        img4 = pygame.transform.scale(img4, (100, 147))  # adjust size
        img5 = pygame.transform.scale(img5, (100, 147))  # adjust size
        img6 = pygame.transform.scale(img6, (100, 147))  # adjust size
        img7 = pygame.transform.scale(img7, (100, 147))  # adjust size
        img8 = pygame.transform.scale(img8, (100, 147))  # adjust size
        img9 = pygame.transform.scale(img9, (100, 147))  # adjust size
        img10 = pygame.transform.scale(img10, (100, 147))  # adjust size
        img11 = pygame.transform.scale(img11, (100, 147))  # adjust size
        img12 = pygame.transform.scale(img12, (100, 147))  # adjust size
        img13 = pygame.transform.scale(img13, (100, 147))  # adjust size
        img14 = pygame.transform.scale(img14, (100, 147))  # adjust size
        img15 = pygame.transform.scale(img15, (100, 147))  # adjust size


        # display figures
        screen.blit(user, (350, 0))  # position
        screen.blit(img1, (100, 150))  # position
        screen.blit(img2, (250, 150))  # position
        screen.blit(img3, (400, 150))  # position
        screen.blit(img4, (550, 150))  # position
        screen.blit(img5, (100, 325))  # position
        screen.blit(img6, (250, 325))  # position
        screen.blit(img7, (400, 325))  # position
        screen.blit(img8, (550, 325))  # position
        screen.blit(img9, (100, 500))  # position
        screen.blit(img10, (250, 500))  # position
        screen.blit(img11, (400, 500))  # position
        screen.blit(img12, (550, 500))  # position
        screen.blit(img13, (100, 675))  # position
        screen.blit(img14, (250, 675))  # position
        screen.blit(img15, (400, 675))  # position


        # setting of font
        font = pygame.font.SysFont("", 30)
        text0 = font.render(file, True, (0, 0, 0))
        screen.blit(text0, (100, 50))  # position

        text1 = font.render("movie" + d0[key - 1], True, (0, 0, 0))
        screen.blit(text1, (100, 125))  # position

        text2 = font.render("movie" + d1[key - 1], True, (0, 0, 0))
        screen.blit(text2, (250, 125))  # position

        text3 = font.render("movie" + d2[key - 1], True, (0, 0, 0))
        screen.blit(text3, (400, 125))  # position

        text4 = font.render("movie" + d3[key - 1], True, (0, 0, 0))
        screen.blit(text4, (550, 125))  # position

        text5 = font.render("movie" + d4[key - 1], True, (0, 0, 0))
        screen.blit(text5, (100, 300))  # position

        text6 = font.render("movie" + d5[key - 1], True, (0, 0, 0))
        screen.blit(text6, (250, 300))  # position

        text7 = font.render("movie" + d6[key - 1], True, (0, 0, 0))
        screen.blit(text7, (400, 300))  # position

        text8 = font.render("movie" + d7[key - 1], True, (0, 0, 0))
        screen.blit(text8, (550, 300))  # position

        text9 = font.render("movie" + d8[key - 1], True, (0, 0, 0))
        screen.blit(text9, (100, 475))  # position

        text10 = font.render("movie" + d9[key - 1], True, (0, 0, 0))
        screen.blit(text10, (250, 475))  # position

        text11 = font.render("movie" + d10[key - 1], True, (0, 0, 0))
        screen.blit(text11, (400, 475))  # position

        text12 = font.render("movie" + d11[key - 1], True, (0, 0, 0))
        screen.blit(text12, (550, 475))  # position

        text13 = font.render("movie" + d12[key - 1], True, (0, 0, 0))
        screen.blit(text13, (100, 650))  # position

        text14 = font.render("movie" + d13[key - 1], True, (0, 0, 0))
        screen.blit(text14, (250, 650))  # position

        text15 = font.render("movie" + d14[key - 1], True, (0, 0, 0))
        screen.blit(text15, (400, 650))  # position

        start = int(time.time())
        while True:
            # for event in pygame.event.get():  # get event
            #     print(int(time.time())-start)
            #     if int(time.time())-start > 5: # check for quit
            #     # if event.type == QUIT:  # check for quit
            #         pygame.quit()
            #         sys.exit()
            pygame.display.update()  # refresh screen
            time.sleep(3)
            pygame.quit()
            break

    show(key)


f = open('display_user.txt', 'r')
target_user = int(f.read().replace('\n', ''))
f.close()

process('train', 'ML100K-TXT-FORAMT/', target_user) # show user's historical (train) items (partly)
process('predict', 'result/', target_user) # show predicted top items for target user (partly)
process('test', 'ML100K-TXT-FORAMT/', target_user) # show the test items for target user (partly)