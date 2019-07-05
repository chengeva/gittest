#ifndef SUBCMD_DATA_H
#define SUBCMD_DATA_H
#define APP	1
#ifdef BIT8
typedef unsigned long u4;
#else
typedef unsigned int u4;
#endif

enum ESUBCMD
{
	e_subcmd_key,
	e_subcmd_unit,
	e_subcmd_set_setting,
	e_subcmd_read_setting,
	e_subcmd_setting_response,
	e_subcmd_start_timer,
	e_subcmd_pause_timer,
	e_subcmd_stop_timer,
	e_subcmd_get_timer,
	e_subcmd_timer_response,
    e_subcmd_ptimer_response,
    e_subcmd_get_ad,
	e_subcmd_ad_response,
	e_subcmd_start_cd,
	e_subcmd_pause_cd,
	e_subcmd_stop_cd,
	e_subcmd_get_cd,
	e_subcmd_cd_response,
	e_subcmd_pcd_response,
};

enum ESETTING_ID
{
	e_setting_sleep = 0,
	e_setting_keydisable,
	e_setting_sound,
	e_setting_resol,
    e_setting_capability,   // korean version 20140826
};

typedef struct SSETTING_CMD
{
	unsigned char	n_set_id;
	unsigned char	n_set_value;
}cmd_setting;

enum EKEY_ID
{
	e_key_tare = 0,
	e_key_zero
};

unsigned char pack_start_timer(char *s_out);
unsigned char pack_pause_timer(char *s_out);
unsigned char pack_stop_timer(char *s_out);
unsigned char pack_get_timer(char *s_out, unsigned char s_time);
unsigned char pack_get_ad(char *s_out);
unsigned long time_to_long(unsigned char n_hr, unsigned char n_min, unsigned char n_sec, unsigned char n_csec);
void long_to_time(const unsigned long n_time, unsigned char *n_hr, unsigned char *n_min, unsigned char *n_sec, unsigned char *n_csec );
unsigned char pack_start_cd(char *s_out, unsigned char n_hr, unsigned char n_min, unsigned char n_sec);
unsigned char pack_pause_cd(char *s_out);
unsigned char pack_stop_cd(char *s_out);
unsigned char pack_get_cd(char *s_out, unsigned char s_time);

void subcmd_handler(unsigned char n_id, unsigned char *s_data);

#endif
