package net.justonedev.mc;

import org.bukkit.block.BlockFace;

public class BlockDataClass {

    public String SignText;
    public BlockFace BlockFaceDirection;
    public boolean BlockActivated;

    public BlockDataClass()
    {
        this.SignText = "";
        this.BlockFaceDirection = BlockFace.SELF;
        this.BlockActivated = false;
    }
    public BlockDataClass(String SignText)
    {
        this.SignText = SignText;
        this.BlockFaceDirection = BlockFace.SELF;
        this.BlockActivated = false;
    }
    public BlockDataClass(boolean BlockActivated)
    {
        this.SignText = "";
        this.BlockFaceDirection = BlockFace.SELF;
        this.BlockActivated = BlockActivated;
    }
    public BlockDataClass(BlockFace FaceDirection)
    {
        this.SignText = "";
        this.BlockFaceDirection = FaceDirection;
        this.BlockActivated = false;
    }
    public BlockDataClass(String SignText, BlockFace FaceDirection)
    {
        this.SignText = SignText;
        this.BlockFaceDirection = FaceDirection;
        this.BlockActivated = false;
    }
    public BlockDataClass(String SignText, boolean BlockActivated)
    {
        this.SignText = SignText;
        this.BlockFaceDirection = BlockFace.SELF;
        this.BlockActivated = BlockActivated;
    }
    public BlockDataClass(BlockFace FaceDirection, boolean BlockActivated)
    {
        this.SignText = "";
        this.BlockFaceDirection = FaceDirection;
        this.BlockActivated = BlockActivated;
    }
    public BlockDataClass(String SignText, BlockFace FaceDirection, boolean BlockActivated)
    {
        this.SignText = SignText;
        this.BlockFaceDirection = FaceDirection;
        this.BlockActivated = BlockActivated;
    }

}
