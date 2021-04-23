	.data
	.align 4
_a: .space 4
	.text
	.globl main
main:
	sw    $ra, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	sw    $fp, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	addu  $fp, $sp, 8
	subu  $sp, $sp, 8
	.data
	.align 4
.L0:	.asciiz "hellaaaa"
	.text
	la    $t0, .L0		#load address
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	.data
	.align 4
.L1:	.asciiz "hellbbbb"
	.text
	la    $t0, .L1		#load address
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	lw    $t1, 4($sp)	#POP
	addu  $sp, $sp, 4
	lw    $t0, 4($sp)	#POP
	addu  $sp, $sp, 4
	lw    $t0, 0($t0)	#load string
	lw    $t1, 0($t1)	#load string
	seq   $t0, $t0, $t1		#perform equality
	sw    $t0, 0($sp)	#PUSH
	subu  $sp, $sp, 4
	lw    $a0, 4($sp)	#POP
	addu  $sp, $sp, 4
	li    $v0, 1
	syscall
	lw    $ra, 0($fp)	#load return address
	move  $t0, $fp		#save control link
	lw    $fp, -4($fp)	#restore FP
	move  $sp, $t0		#restore SP
	jr    $ra		#return
