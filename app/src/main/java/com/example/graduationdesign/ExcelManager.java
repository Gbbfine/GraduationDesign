package com.example.graduationdesign;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;




public class ExcelManager {
    private static final String TAG = ExcelManager.class.getSimpleName();
    private  Context mContext;

    private File documentsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
    private File excelDirectory = new File(documentsDirectory, "data_excel");

    public ExcelManager(Context context) {
        mContext = context;
    }



    public void createExcelFile(String fileName, LinkedHashMap<String,String> map,String id) {

//        String filePath = mContext.getExternalFilesDir(DIRECTORY_DOWNLOADS)+"/"+fileName;
//        File file = new File(mContext.getExternalFilesDir(DIRECTORY_DOWNLOADS),fileName);
        //在外部存储下新建data_excel文件夹
        // 获取文档目录

        if (!excelDirectory.exists()) {
            excelDirectory.mkdirs();
        }
        File file = new File(excelDirectory, fileName);

        XSSFWorkbook workbook;
        XSSFSheet sheet;
        try {
            //如果文件不存在那么新建excel文件和工作簿，如果文件存在则选中存在的文件
            if(!file.exists()){
                //新建工作簿
                workbook = new XSSFWorkbook();
                //新建表单
                sheet = workbook.createSheet("Sheet1");
                //第一行作为标题栏，即要收集的性状特征
                XSSFRow row0 = sheet.createRow(0);
                //第一行第一列记录的参数设定为“ID”
                XSSFCell cell0 = row0.createCell(0);
                //第一行第一列固定存储ID
                cell0.setCellValue("ID");
                //从第一行第二列开始，记录其余性状名称
                int column0 = 1;
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    XSSFCell cell = row0.createCell(column0++);
                    cell.setCellValue(entry.getKey());
                }
            }else{
                //如果文件存在，向工作簿中写入输入流
                FileInputStream fileIn = new FileInputStream(file);
                workbook = new XSSFWorkbook(fileIn);
                //获取表单
                sheet = workbook.getSheet("Sheet1");
            }
            //传入字符串接收数据
            //判断第0行是否有值（即检查之前是否记录过性状
            //获取表单中的第一行
            Sheet sheetTemp = workbook.getSheetAt(0);
            Row rowTemp = sheetTemp.getRow(0);
            //获取第一行的实际单元格数，可以看作是列数
            int physicalNumberOfCells = rowTemp.getPhysicalNumberOfCells();
            //从第一行第二列开始，记录其余性状名称
            int index = 1;//for循环序号
            int columnTemp = physicalNumberOfCells;//用来标记新增加的性状
            for (Map.Entry<String, String> entry : map.entrySet()) {
                for(index=1;index<physicalNumberOfCells;index++){
                    if(rowTemp.getCell(index).getStringCellValue().equals(entry.getKey())){
                        //如果该性状已经在excel中第一行存在，那么跳出循环，检查下一个entry.getkey()是否在第一行记录
                        break;
                    }
                }
                if(index==physicalNumberOfCells){
                    //如果出现新添加的性状，记录到最后一行
                    Cell cellTemp = rowTemp.createCell(columnTemp++);
                    cellTemp.setCellValue(entry.getKey());
                }
            }
//            //第一行作为标题栏，即要收集的性状特征
//            XSSFRow row0 = sheet.createRow(0);
//            //第一行第一列记录的参数设定为“ID”
//            XSSFCell cell0 = row0.createCell(0);
//            cell0.setCellValue("ID");
//            //从第一行第二列开始，记录其余性状名称
//            int column0 = 1;
//            for (Map.Entry<String, String> entry : map.entrySet()) {
//                XSSFCell cell = row0.createCell(column0++);
//                cell.setCellValue(entry.getKey());
//            }
            //如果找不到已记录的id那么默认添加到最后一行
            int targetRowNum = sheet.getLastRowNum() + 1;
            // 遍历每一行的第一个单元格，查找目标内容id
            for (int i = 0; i < sheet.getLastRowNum() + 1; i++) {
                //cellId表示提交的id所在的行
                XSSFRow rowId = sheet.getRow(i);
                Cell cellId = rowId.getCell(0);
                if (cellId != null && cellId.getStringCellValue().equals(id)) {
                    // 找到目标单元格，获取所在行数
                    targetRowNum = cellId.getRowIndex();
                    // 处理找到目标行的逻辑
                    break;
                }
            }
            //如果文件中id已经被记录，那么修改原来记录，如果没有记录，那么新增记录
            XSSFRow row;
            if(targetRowNum == sheet.getLastRowNum() + 1){
                row = sheet.createRow(targetRowNum);
            }else{
                row = sheet.getRow(targetRowNum);
            }

            //不管原来是不是存在id，在选中行的第一列写入id的值
            XSSFCell cell1 = row.createCell(0);
            cell1.setCellValue(id);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                //性状对应在相同的列
                for(index = 1;index<rowTemp.getPhysicalNumberOfCells();index++){
                    if(rowTemp.getCell(index).getStringCellValue().equals(entry.getKey())){
                        XSSFCell cell = row.createCell(index);
                        cell.setCellValue(entry.getValue());
                    }
                }
            }
            //将workbook的内容写进文件
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            workbook.close();
            Log.i(TAG, "Excel file created successfully at " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "Failed to create Excel file", e);
        }
    }

    public int getExcelDataSize(String fileName) throws IOException {

        File file = new File(excelDirectory, fileName);
        // 创建一个Workbook对象，读取Excel文件
        FileInputStream fileIn = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(fileIn);

        Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
        int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum() + 1; // 获取行数
        int nonEmptyCellCount = 0; // 非空单元格数
        for (int i = 1; i < rowCount; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue; // 跳过空行
            }
            Cell firstCell = row.getCell(0);
            if (firstCell == null || firstCell.getStringCellValue().isEmpty()) {
                continue; // 跳过空单元格
            }
            nonEmptyCellCount++; // 增加非空单元格数
        }
        workbook.close();
        return nonEmptyCellCount;
    }

    //个体查询
    public String queryData(String fileName, String id) throws IOException {
        File file = new File(excelDirectory, fileName);
        // 创建一个Workbook对象，读取Excel文件
        FileInputStream fileIn = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(fileIn);
        Sheet sheet = workbook.getSheetAt(0);

        // 获取表头行并查找 "ID" 单元格所在列索引
        Row headerRow = sheet.getRow(0);
        int idColumnIndex = -1;
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null && cell.getStringCellValue().equals("ID")) {
                idColumnIndex = i;
                break;
            }
        }

        if (idColumnIndex == -1) {
            System.out.println("ID 列不存在！");
            return "@";
        }
        String result = "";
        String head = "";
        // 遍历所有数据行并查找与输入的 ID 匹配的行
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            Cell cell = row.getCell(idColumnIndex);
            if (cell != null && cell.getStringCellValue().equals(id)) {
                // 找到与输入的 ID 匹配的行
                //记录表头
                Row rowhead = sheet.getRow(0);
                for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                    Cell dataCell = rowhead.getCell(j);
                    if (dataCell == null) {
                        head += "'"+"'"+"  ";
                    } else {
                        head += "'"+dataCell.toString()+"'"+" ";
                    }
                }
                // 可以在这里获取该行的数据并进行处理
                //System.out.println("找到 ID 为 " + id + " 的行，数据为：");
                for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                    Cell dataCell = row.getCell(j);
                    if (dataCell == null) {
                        result += "'"+"'"+"  ";
                    } else {
                        result += "'"+dataCell.toString()+"'"+" ";
                    }
                }
            }
        }
        workbook.close();
        return head+"@"+result;
    }

    //个体删除
    public int deleteData(String fileName,String id) throws IOException {
        File file = new File(excelDirectory, fileName);
        // 创建一个Workbook对象，读取Excel文件
        FileInputStream fileIn = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(fileIn);
        Sheet sheet = workbook.getSheetAt(0);

        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();
        for (int i = firstRowNum; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell cell = row.getCell(0);
                if (cell != null) {
                    String value = cell.getStringCellValue();
                    if (value.equals(id)) {
                        if(i == sheet.getLastRowNum()){
                            //删除最后一行
                            Row lastRow = sheet.getRow(sheet.getLastRowNum());
                            sheet.removeRow(lastRow);

                        }else{
                            // 删除第rowNum行
                            sheet.shiftRows(i+1, sheet.getLastRowNum(), -1);
                        }
                        FileOutputStream fileOut = new FileOutputStream(file);
                        workbook.write(fileOut);
                        fileIn.close();
                        fileOut.close();
                        return i;
                    }
                }
            }
        }

        return -1; // 没有找到指定 ID 的行
    }
}