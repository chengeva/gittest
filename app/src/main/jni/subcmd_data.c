#include "subcmd_data.h"
#include "cmd_data.h"
#include "en_code.h"

#ifdef BIT8
void long_data_inverse(unsigned long* n_in);
extern unsigned long gn_time;
#else
extern unsigned int gn_time;
#endif

extern unsigned char gn_timercount;
extern unsigned char gb_timerpause;

void subcmd_handler(unsigned char n_id, unsigned char *s_data)
{
	u4 *ln_time, *ln_ad;
	cmd_setting	*lo_setting = (cmd_setting*)s_data;
	switch(n_id)
	{
	
	case e_subcmd_key:		// [app -> scale] general a key event.
		break;
	case e_subcmd_unit:		// [app -> scale] change unit.
		break;
	case e_subcmd_set_setting:		// [app -> scale] set setting.
		break;
	case e_subcmd_read_setting:		// [app -> scale] ask for setting data.
		break;
	case e_subcmd_setting_response:	// [scale -> app] response of e_subcmd_read_setting.
            switch(lo_setting->n_set_id)
		{
            case e_setting_sleep:
                // NSLog(@"sleep setting value=%d", lo_setting->n_set_value);
                break;
            case e_setting_keydisable:
                // NSLog(@"key disable value=%d", lo_setting->n_set_value);
                break;
            case e_setting_sound:
                // NSLog(@"Sound settubg value=%d", lo_setting->n_set_value);
                break;
            case e_setting_resol:
                // NSLog(@"Resolution setting value=%d", lo_setting->n_set_value);
                // debug_value1=lo_setting->n_set_value;
                break;
            case e_setting_capability: // korean version 20140826
                // NSLog(@"receive e_setting_capability: %d",(int) lo_setting->n_set_value);
                break;
		}
            break;
	case e_subcmd_timer_response:
		ln_time = (u4*)s_data;
        // NSLog(@"timer tick (%u)", *ln_time);
		break;
// 12/31
	case e_subcmd_ptimer_response:
		ln_time = (u4*)s_data;
//		go_dlg->add_cmd("pause timer tick", ls_buf);
		break;
	case e_subcmd_ad_response:
		ln_ad = (u4*)s_data;
//		go_dlg->add_cmd("ad value", ls_buf);
		break;
	case e_subcmd_cd_response:
//		sprintf(ls_buf, "hr=%d, min=%d, sec=%d csec=%d", s_data[0], s_data[1], s_data[2], s_data[3] );
//		go_dlg->add_cmd("cd response", ls_buf);
		break;
	case e_subcmd_pcd_response:
//		sprintf(ls_buf, "hr=%d, min=%d, sec=%d csec=%d", s_data[0], s_data[1], s_data[2], s_data[3] );
//		go_dlg->add_cmd("pause cd response", ls_buf);
		break;
	}
}

#ifdef APP
// e_cmd_start_timer
unsigned char pack_start_timer(char *s_out)
{
	unsigned char ln_len = 0;
	char ls_temp = e_subcmd_start_timer;
	ln_len = sr_encrypt(e_cmd_custom, 0, &ls_temp, 2, s_out);
	return ln_len;
}

unsigned char pack_pause_timer(char *s_out)
{
	unsigned char ln_len = 0;
	unsigned char ls_temp = e_subcmd_pause_timer;
	ln_len = sr_encrypt(e_cmd_custom, 0, (char*)&ls_temp, 1, s_out);
	return ln_len;
}

unsigned char pack_stop_timer(char *s_out)
{
	unsigned char ln_len = 0;
	unsigned char ls_temp = e_subcmd_stop_timer;
	ln_len = sr_encrypt(e_cmd_custom, 0, (char*)&ls_temp, 1, s_out);
	return ln_len;
}

unsigned char pack_get_timer(char *s_out, unsigned char s_time)
{
	unsigned char ln_len = 0;
	unsigned char ls_temp[2];
	ls_temp[0] = e_subcmd_get_timer;
	ls_temp[1] = s_time;

	ln_len = sr_encrypt(e_cmd_custom, 0, (char*)ls_temp, 2, s_out);
	return ln_len;
}

// 12/31
unsigned char pack_get_ad(char *s_out)
{
	unsigned char ln_len = 0;
	unsigned char ls_temp = e_subcmd_get_ad;
	ln_len = sr_encrypt(e_cmd_custom, 0, (char*)&ls_temp, 1, s_out);
	return ln_len;
}

unsigned char pack_start_cd(char *s_out, unsigned char n_hr, unsigned char n_min, unsigned char n_sec)
{
	unsigned char ln_len = 0;
	unsigned char ls_temp[4];
	ls_temp[0] = e_subcmd_start_cd;
	ls_temp[1] = n_hr;
	ls_temp[2] = n_min;
	ls_temp[3] = n_sec;

	ln_len = sr_encrypt(e_cmd_custom, 0, (char*)ls_temp, 4, s_out);
	return ln_len;
}

unsigned char pack_pause_cd(char *s_out)
{
	unsigned char ln_len = 0;
	unsigned char ls_temp = e_subcmd_pause_cd;
	ln_len = sr_encrypt(e_cmd_custom, 0, (char*)&ls_temp, 1, s_out);
	return ln_len;
}

unsigned char pack_stop_cd(char *s_out)
{
	unsigned char ln_len = 0;
	unsigned char ls_temp = e_subcmd_stop_cd;
	ln_len = sr_encrypt(e_cmd_custom, 0, (char*)&ls_temp, 1, s_out);
	return ln_len;
}

unsigned char pack_get_cd(char *s_out, unsigned char s_time)
{
	unsigned char ln_len = 0;
	unsigned char ls_temp[2];
	ls_temp[0] = e_subcmd_get_cd;
	ls_temp[1] = s_time;
	ln_len = sr_encrypt(e_cmd_custom, 0, (char*)ls_temp, 2, s_out);
	return ln_len;
}
#endif

#ifdef SCALE
unsigned char pack_timer_r(char *s_out,unsigned char n_id, u4 n_time)
{
	unsigned char ln_len = 0;
	unsigned char ls_temp[5];
// 
#ifdef BIT8
	unsigned char lp_buf[4];

	*((u4*)&ls_temp[1]) = n_time;
	lp_buf[0] = ls_temp[4];
	lp_buf[1] = ls_temp[3];	
	lp_buf[2] = ls_temp[2];
	lp_buf[3] = ls_temp[1];	

	ls_temp[1] = lp_buf[0];
	ls_temp[2] = lp_buf[1];	
	ls_temp[3] = lp_buf[2];
	ls_temp[4] = lp_buf[3];	
#else
	*((u4*)&ls_temp[1]) = n_time;
#endif

	ls_temp[0] = e_subcmd_timer_response;
	ln_len = sr_encrypt(e_cmd_custom, n_id, (char*)ls_temp, 5, s_out);
	return ln_len;
}

unsigned char pack_ptimer_r(char *s_out,unsigned char n_id, u4 n_time)
{
	unsigned char ln_len = 0;
	unsigned char ls_temp[5];
// 
#ifdef BIT8
	unsigned char lp_buf[4];

	*((u4*)&ls_temp[1]) = n_time;
	lp_buf[0] = ls_temp[4];
	lp_buf[1] = ls_temp[3];	
	lp_buf[2] = ls_temp[2];
	lp_buf[3] = ls_temp[1];	

	ls_temp[1] = lp_buf[0];
	ls_temp[2] = lp_buf[1];	
	ls_temp[3] = lp_buf[2];
	ls_temp[4] = lp_buf[3];	
#else
	*((u4*)&ls_temp[1]) = n_time;
#endif
	ln_len = sr_encrypt(e_cmd_custom, n_id, (char*)ls_temp, 5, s_out);
	return ln_len;
}
#endif
