package com.yeel.drc.localdb;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.yeel.drc.api.synccontact.SyncContactData;

import java.util.List;

@Dao
public interface ContactDao {
    @Insert
    void insertAll(List<SyncContactData> syncContactData);

    @Query("SELECT * FROM SyncContactData")
    List<SyncContactData> getAll();

    @Query("DELETE FROM SyncContactData")
    void deleteAll();
}
