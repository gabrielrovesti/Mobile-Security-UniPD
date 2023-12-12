#!/bin/sh

hashcat -a 3 -m 0 82f5c1c9be89c68344d5c6bcf404c804 "?l?l?l" --show
hashcat -a 3 -m 0 e86d706732c0578713b5a2eed1e6fb81 "?l?l?l?l?l?l" --show
hashcat -a 3 -m 0 7ff1301675eb857f345614f9d9e47c89 "?u?u?u?u?u" --show
hashcat -a 3 -m 0 b446830c23bf4d49d64a5c753b35df9a "?d?d?l?u?l?d?d?d?d" --show