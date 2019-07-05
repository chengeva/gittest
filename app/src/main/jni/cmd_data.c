#include "subcmd_data.h"
#include "en_code.h"
#ifdef BIT8
void long_data_inverse(unsigned int* n_in)
{
	char lp_buf[4];
	char *lp_input = (char*)n_in;
	lp_buf[0] = lp_input[3];
	lp_buf[1] = lp_input[2];
	lp_buf[2] = lp_input[1];
	lp_buf[3] = lp_input[0];

	lp_input[0] = lp_buf[0];
	lp_input[1] = lp_buf[1];
	lp_input[2] = lp_buf[2];
	lp_input[3] = lp_buf[3];
}
#endif

char check_cmdlen(unsigned char n_cmd, unsigned char n_len)
{
	switch(n_cmd)
	{
        default:
            return 1;
        case e_cmd_custom:
        case e_cmd_file:
        case e_cmd_str:
            return 0;
            // 20140725 -s
        case e_cmd_info_sent:
            return (n_len == INFO_LEN)?0:1;
        case e_cmd_isp:
        case e_cmd_info_get:
            // 20140725 -e
        case e_cmd_battery:
            return (n_len == 0)?0:1;

        case e_cmd_battery_r:
            return (n_len == BATTERY_R_LEN)?0:1;

        case e_cmd_weight:
            return (n_len == WEIGHT_LEN)?0:1;

        case e_cmd_weight_r:
            return (n_len == WEIGHT_R_LEN)?0:1;

        case e_cmd_weight_r2:
            return (n_len == WEIGHT_R2_LEN)?0:1;

        case e_cmd_tare:
            return (n_len == TARE_LEN)?0:1;

        case e_cmd_sound:
            return (n_len == SOUND_LEN)?0:1;

        case e_cmd_sound_on:
            return (n_len == AUDIO_LEN)?0:1;

        case e_cmd_light_on:
            return (n_len == LIGHT_LEN)?0:1;
	}
}

// e_cmd_str
unsigned char pack_string(char *s_out, char *s_in, unsigned char n_strlen)
{
	unsigned char n_len;
	n_len = sr_encrypt(e_cmd_str, 0, s_in, n_strlen, s_out);
	return n_len;
}

unsigned char pack_custom(char *s_out, unsigned char n_customid, unsigned char* s_data, unsigned char n_len)
{
	unsigned char ln_len = 0, ln_loop = 0;
	unsigned char ls_temp[64];
	ls_temp[0] = n_customid;
	for(ln_loop =0; ln_loop < n_len; ln_loop ++)
		ls_temp[1 + ln_loop] = s_data[ln_loop];
	ln_len = sr_encrypt(e_cmd_custom, 0, (char*)ls_temp, n_len + 1, s_out);
	return ln_len;
}

unsigned char pack_setting(char *s_out, unsigned char n_settingid, unsigned char n_value)
{
	unsigned char ln_len = 0;
	unsigned char ls_temp[3];
	ls_temp[0] = e_subcmd_setting_response;
	ls_temp[1] = n_settingid;
	ls_temp[2] = n_value;
	ln_len = sr_encrypt(e_cmd_custom, 0, (char*)ls_temp, 3, s_out);
	return ln_len;
}

#ifdef SCALE

// e_cmd_battery_r
unsigned char pack_battery(char *s_out, unsigned char n_battery)
{
	bat_rep lo_rep;
	unsigned char n_len;

	lo_rep.n_battery = n_battery;
	n_len = sr_encrypt(e_cmd_battery_r, 0, (char*)&lo_rep, BATTERY_R_LEN, s_out);
	return n_len;
}


// e_cmd_weight_r
unsigned char pack_weight(char *s_out, unsigned char n_id, unsigned int n_data, unsigned char n_dp, unsigned char n_unit,
				 unsigned char n_stable, unsigned char n_positive, unsigned char n_type)
{
	wt_rep lo_wt;
	unsigned char  n_len = 0;

	if (n_stable == e_wt_unstable)
		lo_wt.b_stable = e_wt_unstable;
	else
		lo_wt.b_stable = e_wt_stable;

	if (n_positive == e_wt_negative)
		lo_wt.b_positive = e_wt_negative;
	else
		lo_wt.b_positive = e_wt_positive;

	lo_wt.n_type = n_type;

	lo_wt.n_unit = n_unit;
	lo_wt.n_data = n_data;
	lo_wt.n_dp = n_dp;
#ifdef BIT8
long_data_inverse(&lo_wt.n_data);
#endif
	n_len = sr_encrypt(e_cmd_weight_r, n_id, (char*)&lo_wt, WEIGHT_R_LEN, s_out);
	return n_len;
}

unsigned char pack_weight2(char *s_out, unsigned char n_id,unsigned int n_data1, unsigned char n_dp1, unsigned int n_data2, unsigned char n_dp2,
						   unsigned char n_unit, unsigned char n_stable, unsigned char n_positive, unsigned char n_type)
{
	wt_rep2 lo_wt;
	unsigned char  n_len = 0;

	if (n_stable == e_wt_unstable)
		lo_wt.b_stable = e_wt_unstable;
	else
		lo_wt.b_stable = e_wt_stable;

	if (n_positive == e_wt_negative)
		lo_wt.b_positive = e_wt_negative;
	else
		lo_wt.b_positive = e_wt_positive;

	lo_wt.n_type = n_type;

	lo_wt.n_unit = n_unit;
	lo_wt.n_data1 = n_data1;
	lo_wt.n_dp1 = n_dp1;
	lo_wt.n_data2 = n_data2;
	lo_wt.n_dp2 = n_dp2;

#ifdef BIT8
long_data_inverse(&lo_wt.n_data1);
long_data_inverse(&lo_wt.n_data2);
#endif
	n_len = sr_encrypt(e_cmd_weight_r2, n_id, (char*)&lo_wt, WEIGHT_R2_LEN, s_out);
	return n_len;
}
#endif
#ifdef APP

// e_cmd_battery
unsigned char pack_battery_req(char *s_out)
{
	unsigned char n_len;
	n_len = sr_encrypt(e_cmd_battery, 0, 0, 0, s_out);
	return n_len;
}

// e_cmd_weight
unsigned char pack_weight_req(char *s_out, unsigned char n_period, unsigned char n_time, unsigned char n_type)
{
	wt_req lo_req;
	unsigned char n_len;
	lo_req.n_period = n_period;
	lo_req.n_time = n_time;
	lo_req.n_type = n_type;

	n_len = sr_encrypt(e_cmd_weight, 0, (char*)&lo_req, WEIGHT_LEN, s_out);
	return n_len;
}

// e_cmd_tare
unsigned char pack_tare(char *s_out, unsigned int n_data, unsigned char n_dp, unsigned char n_unit)
{
	tare_req lo_tare;
	unsigned char  n_len = 0;

	lo_tare.n_data = n_data;
	lo_tare.n_dp = n_dp;
	lo_tare.n_unit = n_unit;

	n_len =sr_encrypt(e_cmd_tare, 0, (char*)&lo_tare, TARE_LEN, s_out);
	return n_len;
}

// e_cmd_sound
unsigned char pack_sound_req(char *s_out, unsigned char n_period, unsigned char n_time)
{
	snd_req lo_req;
	unsigned char n_len;

	lo_req.n_period = n_period;
	lo_req.n_time = n_time;

	n_len = sr_encrypt(e_cmd_sound, 0, (char*)&lo_req, SOUND_LEN, s_out);
	return n_len;
}

// e_cmd_sound_on
unsigned char pack_audio_req(char *s_out, unsigned char n_on)
{
	aud_req lo_req;
	unsigned char n_len;

	lo_req.n_on = n_on;

	n_len = sr_encrypt(e_cmd_sound_on, 0, (char*)&lo_req, AUDIO_LEN, s_out);
	return n_len;
}

// e_cmd_light_on
unsigned char pack_light_req(char *s_out, unsigned char n_on, unsigned char n_time)
{
	light_req lo_req;
	unsigned char n_len;

	lo_req.n_on = n_on;
	lo_req.n_time = n_time;

	n_len = sr_encrypt(e_cmd_light_on, 0, (char*)&lo_req, LIGHT_LEN, s_out);
	return n_len;
}
// 20140725 -s
// e_cmd_isp
unsigned char pack_isp_req(char *s_out)
{
	unsigned char n_len;
	n_len = sr_encrypt(e_cmd_isp, 0, 0, 0, s_out);
	return n_len;
}
// e_cmd_info_get
unsigned char pack_info_req(char *s_out)
{
	unsigned char n_len;
	n_len = sr_encrypt(e_cmd_info_get, 0, 0, 0, s_out);
	return n_len;
}
// 20140725 -e

#endif
