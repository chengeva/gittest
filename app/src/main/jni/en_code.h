#ifndef EN_CODE_H
#define EN_CODE_H
#include "cmd_data.h"
extern const unsigned char s_table[];
extern const unsigned char s_table2[];

enum E_ENCRY_RESULT
{
	e_encry_success = 0,
	e_encry_fail,
	e_encry_tobe_continue,
	e_encry_sum_error,
	e_encry_protocol_error,
	e_encry_len_error,
};

enum  EENC_TYPE
{
	e_enct_str = 1,
	e_enct_end
};
unsigned char sr_encrypt(unsigned char n_cmd, unsigned char n_id, char *s_in, unsigned char n_len, char *s_out);
unsigned char sr_decrypt(char *s_in, unsigned char n_in, char *s_out, unsigned char* n_cmd, unsigned char* n_id, unsigned char* n_index);
#endif
