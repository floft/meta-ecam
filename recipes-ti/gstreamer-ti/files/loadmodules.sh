#!/bin/sh
#
# CMEM Allocation
#    1x5250000            Circular buffer
#    6x829440,1x691200    Video buffers (max D1 PAL)
#    1x345600             Underlying software components (codecs, etc.)
#    1x1                  Dummy buffer used during final flush

rmmod cmemk 2>/dev/null

# Original, works with video:
#modprobe cmemk allowOverlap=1 phys_start=0x86300000 phys_end=0x87300000 \
#        pools=1x5250000,6x829440,1x345600,1x691200,1x1

# Modified to work with full-res images:
modprobe cmemk allowOverlap=1 phys_start=0x83700000 phys_end=0x87300000 \
            pools=2x20155392,1x7558872,1x5250000,6x829440,1x345600,1x691200,1x1

# insert DSP/BIOS Link driver
modprobe dsplinkk

# insert Local Power Manager driver
modprobe lpm_omap3530

# insert SDMA driver
modprobe sdmak
