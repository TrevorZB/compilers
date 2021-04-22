	.data
	.align 4
_b: .space 4
	.text
	.globl main
main:
	sw    $ra, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	sw    $fp, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	addu  $fp, $sp, 12
	subu  $sp, $sp, 12
	li    $t0, 10
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	la    $t0, -12($fp)	#load local address
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	lw    $t0, 4($sp)	#POP
	addu  $sp, $sp, 4
	lw    $t1, 4($sp)	#load value
	sw    $t1, 0($t0)	#store value
	addu  $sp, $sp, 4
	li    $t0, 7
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	la    $t0, _b		#load global address
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	lw    $t0, 4($sp)	#POP
	addu  $sp, $sp, 4
	lw    $t1, 4($sp)	#load value
	sw    $t1, 0($t0)	#store value
	addu  $sp, $sp, 4
	lw    $t0, -12($fp)	#load local
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	lw    $a0, 4($sp)	#POP
	addu  $sp, $sp, 4
	li    $v0, 1
	syscall
	lw    $t0, _b		#load global
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	lw    $a0, 4($sp)	#POP
	addu  $sp, $sp, 4
	li    $v0, 1
	syscall
	lw    $ra, -4($fp)	#load return address
	move  $t0, $fp		#save control link
	lw    $fp, -8($fp)	#restore FP
	move  $sp, $t0		#restore SP
	jr    $ra		#return
