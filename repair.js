// TimePeriod 数据丢失时，可以通过运行本文件恢复出 INSERT 语句

// 把日志中的时段数组拷贝到这里
let thisWeekFromLog = [];

const stateMap = {
    "MINE": 1,
    "OCCUPIED": 1,
    "PASSED": 1,
    "DISABLED_RED": 10,
    "DISABLED_WARM": 11,
    "DISABLED_COOL": 12,
}

for (let hourRow of thisWeekFromLog) {
    for (let timePeriod of hourRow) {
        if (null === timePeriod.userId && timePeriod.showText === "") {
            continue;
        }
        let id = timePeriod.id + '';
        let roomId = id % 100;
        let dateStr = `${id.substr(0,4)}.${id.substr(4,2)}.${id.substr(6,2)} ${id.substr(8, 2)}:${id.substr(10, 2)}:${id.substr(12, 2)}`;
        // let date = new Date(dateStr);
        
        let statusCode = stateMap[timePeriod.state];

        // if (statusCode === 1) {
        //     continue;
        // }
        
        let sql = `INSERT INTO \`key3\`.\`time_period\`(\`id\`, \`room_id\`, \`show_text\`, \`start_time\`, \`state\`, \`user_id\`) VALUES (${id}, ${roomId}, '${timePeriod.showText}', '${dateStr}', ${statusCode}, ${timePeriod.userId});`
        console.log(sql)
    }
}