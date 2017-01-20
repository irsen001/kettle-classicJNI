#define MSMGPIO_IOC_MAGIC  					'k'
#define MSMGPIO_OTG_GPIO_ON					_IO(MSMGPIO_IOC_MAGIC, 19)
#define MSMGPIO_OTG_GPIO_OFF				_IO(MSMGPIO_IOC_MAGIC, 20)
#define MSMGPIO_BD_POWERON   				_IO(MSMGPIO_IOC_MAGIC, 16)
#define MSMGPIO_BD_POWEROFF					_IO(MSMGPIO_IOC_MAGIC, 17)
#define MSMGPIO_UART_BD						_IO(MSMGPIO_IOC_MAGIC, 18)

#define TEA_IOCTL_MAGIC_NO			0xF0
/*#define TEA_IOCTL_POWNON			    _IOW(TEA_IOCTL_MAGIC_NO, 0, unsigned)
#define TEA_IOCTL_PUMP_WATER		    _IOW(TEA_IOCTL_MAGIC_NO, 1, unsigned)
#define TEA_IOCTL_AUTO_PUMP_WATER		_IOW(TEA_IOCTL_MAGIC_NO, 2, unsigned)
#define TEA_IOCTL_BURNING			    _IOW(TEA_IOCTL_MAGIC_NO, 3, unsigned)*/
#define TEA_IOCTL_POWNON			    _IO(TEA_IOCTL_MAGIC_NO, 0)
#define TEA_IOCTL_PUMP_WATER		    _IO(TEA_IOCTL_MAGIC_NO, 1)
#define TEA_IOCTL_AUTO_PUMP_WATER		_IO(TEA_IOCTL_MAGIC_NO, 2)
#define TEA_IOCTL_BURNING			    _IO(TEA_IOCTL_MAGIC_NO, 3)

#define K10_ONOFF   0
#define K10_AUTO_WATER   1
#define K10_PUMP_WATER   2
#define K10_HEATING   3
#define K10_TEMP    4

/*int t_arry[15][2]={
    {25,1050},//0
    {26,1039},
    {27,1028},
    {28,1017},
    {29,1006},
    {30,995},
    {31,984},
    {32,973},
    {33,962},
    {34,951},
    {35,940},//10
    {36,929},
    {37,918},
    {38,907},
    {39,896},
    {40,885},//15
    {41,874},
    {42,863},
    {43,852},
    {44,841},
    {45,830},//20
    {46,819},
    {47,808},
    {48,797},
    {49,786},
    {50,775},//25
    {51,},
    {52,},
    {53,},
    {54,},
    {55,},
    {56,},
    {57,},
    {58,},
    {59,},
    {60,},
    {61,},
    {62,},
    {63,},



    {45,816},
    {50,760},
    {55,620},//4
    {60,585},
    {65,500},
    {70,420},
    {75,385},
    {80,365},
    {85,340},//10
    {90,280},
    {95,235},
    {100,195},
    {0,0}//14
};*/