package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.undo.UndoableEdit;

import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascontrol.commonmenu.PIMActionName;

/***/
public class UndoRedoAction extends SAction {
    private static UndoRedoAction instance;

    public static UndoRedoAction getInstance() {
        if (instance == null) {
            instance = new UndoRedoAction();
        }
        return instance;
    }

    /** Creates a new instance of UndoAction */
    private UndoRedoAction() {
        super(IStatCons.HAVE_UNDO);
    }

    public static UndoRedoAction getUndoAction() {
        if (undoAction == null) {
            undoAction = new UndoRedoAction(IStatCons.HAVE_UNDO);
        }
        return undoAction;
    }

    public static UndoRedoAction getRedoAction() {
        if (redoAction == null) {
            redoAction = new UndoRedoAction(IStatCons.HAVE_REDO);
        }
        return redoAction;
    }

    /** Creates a new instance of UndoAction */
    private UndoRedoAction(int flag) {
        super(flag);
        isUndo = flag == IStatCons.HAVE_UNDO;
    }

    /**
     * 只支持一次Undo或Redo。如果做了某个动作（包括Redo了某个动作），此时菜单上将显示Undo，Undo了某个动作时， 菜单上将显示Redo。
     * 
     * @TODO:以后改成支持无数次UndoRedo。
     */
    public void actionPerformed(
            ActionEvent e) {
        // 有edit可undo/redo。
        // 此判断实际不会为false，因为若无edit，action本身为disable。
        // if (edit != null)
        // {
        // if (hasUndo) //已undo，则redo
        // {
        // edit.redo();
        // putValue(Action.NAME, edit.getUndoPresentationName());
        // }
        // else //已redo，则undo
        // {
        // edit.undo();
        // putValue(Action.NAME, edit.getRedoPresentationName());
        // }
        // //更新undo标志
        // hasUndo = !hasUndo;
        // }
        // edit = (UndoableEdit)editList.get(undoRedoIndex);
        if (isUndo) {
            edit = (UndoableEdit) editList.get(undoRedoIndex);
            edit.undo();
            undoRedoIndex++;
            if (undoRedoIndex == editList.size()) {
                setEnabled(false);
            }
        } else {
            edit = (UndoableEdit) editList.get(undoRedoIndex - 1);
            edit.redo();
            undoRedoIndex--;
            if (undoRedoIndex == 0) {
                setEnabled(false);
            }
        }
        if (undoRedoIndex > 0) {
            PIMActionName.ACTIONS[PIMActionName.ID_EDIT_REDO].setEnabled(true);
        }
        if (undoRedoIndex < editList.size()) {
            PIMActionName.ACTIONS[PIMActionName.ID_EDIT_UNDO].setEnabled(true);
        }
    }

    /**
     * 向UndoManager添加undo事件。
     * 
     * @param anEdit
     *            加入UndoManager的edit事件。
     * @return true 成功加入。
     * @Called by: ModelUndoMethod
     */
    public boolean addEdit(
            UndoableEdit prmEdit) {
        // //清理
        // if (edit != null)
        // {
        // edit.die();
        // }
        // //判断是否edit为空
        // if (prmEdit != null)
        // {
        // //更新edit
        // edit = prmEdit;
        // //设置undo标志为未undo
        // hasUndo = false;
        // //更新action name
        // putValue(Action.NAME, edit.getUndoPresentationName());
        // //设置action为enable
        // setEnabled(true);
        //
        // return true;
        // }
        // else
        // {
        // //edit为空则视为清除edit
        // discardAllEdits();
        // return false;
        // }
        for (int i = 0; i < undoRedoIndex; i++) {
            ((UndoableEdit) (editList.get(i))).die();
            editList.remove(i);
        }
        editList.trimToSize();
        if (prmEdit != null) {
            editList.add(0, prmEdit);
            setEnabled(true);
            // putValue(Action.NAME, prmEdit.getUndoPresentationName());
        }
        undoRedoIndex = 0;
        return true;
    }

    /**
     * 清空Undo Manager中的Undo Edit。
     * 
     * @Called by: ModelUndoMethod
     */
    public void discardAllEdits() {
        for (int i = 0; i < editList.size(); i++) {
            ((UndoableEdit) (editList.get(i))).die();
        }
        editList.clear();
        undoRedoIndex = 0;
        // putValue(Action.NAME, "撤销");
        setEnabled(false);
        PIMActionName.ACTIONS[PIMActionName.ID_EDIT_UNDO].setEnabled(false);
        PIMActionName.ACTIONS[PIMActionName.ID_EDIT_UNDO].setEnabled(false);
        // //清除
        // edit.die();
        // edit = null;
        // //更新action name
        // putValue(Action.NAME, "撤销");
        // //设置action为disable
        // setEnabled(false);
    }

    /**
     * call by : PIMControl用于“标志Undo/Redo的状态” 此方法为了能够更好的控制Undo/Redo状态而添加
     */
    public UndoableEdit getUndoRedoEdit() {
        if (editList.size() > 0) {
            // A.s("get instance edit");
            return (UndoableEdit) editList.get(0);
        }
        // A.s("get null edit ");
        return null;
    }

    public UndoableEdit getUndoEdit() {
        int size = editList.size();
        if (size == 0 || undoRedoIndex == size) {
            return null;
        }
        return (UndoableEdit) editList.get(0);
    }

    public UndoableEdit getRedoEdit() {
        int size = editList.size();
        if (size == 0 || undoRedoIndex == 0) {
            return null;
        }
        return (UndoableEdit) editList.get(0);
    }

    /** UndoEdit的引用 */
    private UndoableEdit edit;

    private boolean isUndo;

    private static UndoRedoAction undoAction;
    private static UndoRedoAction redoAction;
    private static Vector editList = new Vector();
    private static int undoRedoIndex;
}
